package com.github.xukaiquan.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xukaiquan.course.model.HttpException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice //全局的
public class ErrorHandingController {
    ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler({HttpException.class})
    //任何丢出这个异常的地方都会统一的被这个方法处理
    public void handleError(HttpServletResponse response, HttpException exception) throws Exception {
        response.setStatus(exception.getStatusCode());

        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("message", exception.getMessage());

        response.getOutputStream().write(objectMapper.writeValueAsBytes(jsonObject));
        response.getOutputStream().flush();
    }

}
