package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {
    User findUsersByUsername(String username);

    @Query(value = "select u from User u where u.username like %:search%")
    Page<User> findBySearch(@Param("search") String search, Pageable pageable);


}
