package com.entity;

import lombok.Data;

@Data
public class Result<T> {
    private int code; // 状态码：0成功，1失败
    private String message; // 提示信息
    private T data; // 数据

    // 成功返回结果
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 失败返回结果
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        return result;
    }
}