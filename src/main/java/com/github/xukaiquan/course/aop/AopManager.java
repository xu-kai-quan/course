package com.github.xukaiquan.course.aop;

import com.github.xukaiquan.course.configuration.Config;
import com.github.xukaiquan.course.model.HttpException;
import com.github.xukaiquan.course.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AopManager {
    @Around("@annotation(com.github.xukaiquan.course.annotation.Admin)")//@Admin标识的方法都被我管起来
    public Object checkAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = Config.UserContext.getCurrentUser();
        if (currentUser == null) {
            throw new HttpException(401, "没有登录！");
        } else if (currentUser.getRoles().stream().anyMatch(role -> "管理员".equals(role.getName()))) {
            return joinPoint.proceed();
        } else {
            throw new HttpException(403, "没有权限！");
        }
    }
}
