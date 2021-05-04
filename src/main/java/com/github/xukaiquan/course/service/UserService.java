package com.github.xukaiquan.course.service;

import com.github.xukaiquan.course.annotation.Admin;
import com.github.xukaiquan.course.dao.RoleDao;
import com.github.xukaiquan.course.dao.UserDao;
import com.github.xukaiquan.course.model.HttpException;
import com.github.xukaiquan.course.model.PageResponse;
import com.github.xukaiquan.course.model.Role;
import com.github.xukaiquan.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;


@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    RoleDao roleDao;
    @Admin
    public PageResponse<User> getAllUsers(String search,
                                          Integer pageSize,
                                          Integer pageNum,
                                          String orderBy,
                                          String orderType) {
        if (orderBy != null && orderType == null) {
            orderType = "Asc";
        }
        Pageable pageable =
                orderBy == null
                        ? PageRequest.of(pageNum - 1, pageSize)
                        : PageRequest.of(pageNum - 1, pageSize,
                        Sort.by(Sort.Direction.fromString(orderType), orderBy));

        if (!StringUtils.hasLength(search)) {
            return toOurPageResponse(userDao.findAll(pageable), pageNum, pageSize);
        } else {
            return toOurPageResponse(userDao.findBySearch(search, pageable), pageNum, pageSize);
        }
    }

    private PageResponse<User> toOurPageResponse(Page<User> users, int pageNum, int pageSize) {
        return new PageResponse<>(users.getTotalPages(), pageNum, pageSize, users.getContent());
    }


    @Admin
    public User getUser(Integer id) {
        return userDao
                .findById(id)
                .orElseThrow(() -> new HttpException(404, "Not found" + id));

    }

    @Admin
    public User updateUser(Integer id, User user) {
        var nameToRoleMap =
        roleDao.findAll().stream().collect(Collectors.toMap(Role::getName,r -> r));

        user.getRoles().forEach(role -> role.setId(
                nameToRoleMap.get(role.getName()).getId()
        ));

        User userInDatabase = getUser(id);
        userInDatabase.setRoles(user.getRoles());
        userDao.save(userInDatabase);
        return userInDatabase;
    }
}
