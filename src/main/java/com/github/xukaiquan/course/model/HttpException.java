package com.github.xukaiquan.course.model;

/**
 * 任何方法抛出这个异常，意味着HTTP请求应该返回对应的值
 */

public class HttpException extends RuntimeException {
    private Integer statusCode;
    private String message;

    public HttpException(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
