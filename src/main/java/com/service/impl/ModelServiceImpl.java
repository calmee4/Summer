package com.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.service.ModelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ModelServiceImpl implements ModelService {

    @Value("${model.api-key}")
    private String apiKey;

    @Value("${model.base-url}")
    private String baseUrl;

    @Value("${model.model-name}")
    private String modelName;

    @Value("${model.temperature}")
    private double temperature;

    @Override
    public String getModelResponse(String message) {
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        requestBody.put("temperature", temperature);

        // 消息结构
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        // 使用 UTF-8 编码发送请求
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // 发送请求并获取响应
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            String response = responseEntity.getBody();

            if (response == null) {
                throw new RuntimeException("API调用返回空响应");
            }

            // 解析响应
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (!jsonObject.containsKey("choices")) {
                throw new RuntimeException("响应中未包含choices字段: " + response);
            }

            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices.isEmpty()) {
                throw new RuntimeException("choices为空");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObj = firstChoice.getJSONObject("message");
            String content = messageObj.getString("content");

            if (content == null) {
                throw new RuntimeException("响应中未包含内容");
            }

            // 处理内容中的 <think> 标签
            return content.replaceAll("(?s)<think>.*?</think>", "").trim();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 捕获HTTP错误并打印日志
            throw new RuntimeException("API请求错误: " + e.getMessage(), e);
        }
    }
}


