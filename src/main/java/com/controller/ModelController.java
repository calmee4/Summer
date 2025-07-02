package com.controller;

import com.entity.ChatRequest;
import com.entity.Result;
import com.service.ModelService;
import com.annotation.IgnoreAuth; // 将import移至此处
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private ModelService modelService;

    @IgnoreAuth // 添加注解允许匿名访问
    @PostMapping("/chat")
    public Result chat(@RequestBody ChatRequest request) {
        String response = modelService.getModelResponse(request.getMessage());
        return Result.success(response);
    }
}
