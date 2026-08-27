package com.enterprise.tasksuperviseserver.common.result;

import lombok.Data;

/**
 * 统一响应结果封装类
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权", null);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(403, "无权限", null);
    }

    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }
}
