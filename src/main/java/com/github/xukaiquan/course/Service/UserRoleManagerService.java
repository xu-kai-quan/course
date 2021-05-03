package com.github.xukaiquan.course.Service;

import com.github.xukaiquan.course.annotation.Admin;
import com.github.xukaiquan.course.dao.userDao;
import com.github.xukaiquan.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理用户的角色，要求当前登录的用户必须是管理员
 */

@Service
public class UserRoleManagerService {
    @Autowired
    userDao userDao;

    @Admin
    public List<User> getAllUsers(){
        return userDao.findAll();
    }

}
