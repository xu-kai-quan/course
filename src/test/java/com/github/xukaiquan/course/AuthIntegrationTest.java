package com.github.xukaiquan.course;

import com.github.xukaiquan.course.model.Session;
import com.github.xukaiquan.course.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
public class AuthIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void registerLogout() throws IOException, InterruptedException {
        //注册用户
        String usernameAndPassword = "username=zhangsan&password=123456";

        HttpResponse<String> response = post("/user", APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE, usernameAndPassword);

        User responseUser = objectMapper.readValue(response.body(), User.class);

        assertEquals(201, response.statusCode());
        assertEquals("zhangsan", responseUser.getUsername());
        assertNull(responseUser.getEncryptedPassword());

        //用该用户进行登录
        response = post("/session", APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE, usernameAndPassword);
        responseUser = objectMapper.readValue(response.body(), User.class);

        //现在在HTTP响应里应该有一个Set-Cookie,作为下次发起请求的凭证
        String cookie = response.headers()
                .firstValue("Set-Cookie")
                .get();

        assertNotNull(cookie);
        assertEquals(200, response.statusCode());
        assertEquals("zhangsan", responseUser.getUsername());
        assertNull(responseUser.getEncryptedPassword());

        //确定该用户已经登录
        response = get("/session", cookie);
        assertEquals(200, response.statusCode());
        Session session = objectMapper.readValue(response.body(), Session.class);
        assertEquals("zhangsan", session.getUser().getUsername());

        //调用注销接口
        response = delete("/session", cookie);
        assertEquals(204, response.statusCode());

        //再次尝试访问用户的登陆状态
        //确定该用户已经登出
        response = get("/session", cookie);
        assertEquals(401, response.statusCode());

    }

    @Test
    public void getErrorIfUsernameAlreadyRegistered() throws IOException, InterruptedException {
        //注册用户
        String usernameAndPassword = "username=zhangsan&password=123456";
        HttpResponse<String> response = post("/user", APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE, usernameAndPassword);
        assertEquals(201, response.statusCode());

        //成功
        //再次使用同名用户注册
        //失败
        response = post("/user", APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE, usernameAndPassword);
        assertEquals(409, response.statusCode());
    }

    @Test
    public void onlyAdminCanSeeAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/admin/users", adminUserCookie);
        assertEquals(200, response.statusCode());
    }

    @Test
    public void nonAdminCanNotSeeAllUsers() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/admin/users", studentUserCookie);
        assertEquals(403, response.statusCode());
    }


    public void get401IfNoPermission() {

    }

}
