package com.github.xukaiquan.course;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.xukaiquan.course.model.PageResponse;
import com.github.xukaiquan.course.model.Role;
import com.github.xukaiquan.course.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void get401IfNotLogIn() throws IOException, InterruptedException {
        assertEquals(401, get("/user").statusCode());
        assertEquals(401, get("/user?search=a&pageSize=10&pageNum=1").statusCode());
        assertEquals(401, get("/user/1").statusCode());
    }
    @Test
    public void get403IfNotAdmin() throws IOException, InterruptedException {
        assertEquals(403, get("/user", studentUserCookie).statusCode());
        assertEquals(403, get("/user/1", studentUserCookie).statusCode());
        assertEquals(403, get("/user", teacherUserCookie).statusCode());
        assertEquals(403, get("/user/1", teacherUserCookie).statusCode());
        assertEquals(403, patch("/user/1", "{}", Map.of("Cookie", teacherUserCookie)).statusCode());
        assertEquals(403, patch("/user/1", "{}", Map.of("Cookie", studentUserCookie)).statusCode());
    }
    @Test
    public void adminCanGetAllUsers() throws IOException, InterruptedException {
        var body = get("/user?pageSize=1&pageNum=2&orderBy=id&orderType=Asc",adminUserCookie).body();
        var pageResponse = objectMapper.readValue(body,
                new TypeReference<PageResponse<User>>() {});
        assertEquals(1,pageResponse.getPageSize());
        assertEquals(3,pageResponse.getTotalPage());
        assertEquals(2,pageResponse.getPageNum());
        assertEquals(1,pageResponse.getData().size());
        assertEquals("Teacher2",pageResponse.getData().get(0).getUsername());
    }
    @Test
    public void adminCanSearchUser() throws IOException, InterruptedException {
        var body = get("/user?pageSize=100&pageNum=1&search=e&orderBy=id&orderType=Desc",adminUserCookie).body();
        var pageResponse = objectMapper.readValue(body,
                new TypeReference<PageResponse<User>>() {});
        assertEquals(100,pageResponse.getPageSize());
        assertEquals(1,pageResponse.getTotalPage());
        assertEquals(1,pageResponse.getPageNum());
        assertEquals(2,pageResponse.getData().size());
        assertEquals(Arrays.asList("Teacher2","Student1"),
                pageResponse.getData().stream().map(User::getUsername).collect(toList()));
    }
    @Test
    public void adminCanUpdateUserRole() throws IOException, InterruptedException {
        String studentJson = get("/user/1",adminUserCookie).body();
        User student = objectMapper.readValue(studentJson,User.class);
        Role role = new Role();
        role.setName("管理员");
        student.getRoles().add(role);

        var status = patch("/user/1",
                objectMapper.writeValueAsString(student),
                Map.of("Cookie",adminUserCookie)).statusCode();
        assertEquals(200,status);
        //再次获取一下，现在用户已经是管理员了
        studentJson = get("/user/1",adminUserCookie).body();
        student = objectMapper.readValue(studentJson,User.class);

        assertTrue(student.getRoles().stream().anyMatch(r -> r.getName().equals("管理员")));

    }
}
