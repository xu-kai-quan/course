package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.Status;
import com.github.xukaiquan.course.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    //不是SQL 而是 JPQL
//    @Query("SELECT u FROM User u WHERE u.username =?1 and u.encryptedPassword =?2")
    User findUsersByUsername(String username);


}
