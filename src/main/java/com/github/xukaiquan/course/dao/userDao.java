package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userDao extends JpaRepository<User, Integer> {
    //不是SQL 而是 JPQL
//    @Query("SELECT u FROM User u WHERE u.username =?1 and u.encryptedPassword =?2")
    User findUsersByUsername(String username);


}
