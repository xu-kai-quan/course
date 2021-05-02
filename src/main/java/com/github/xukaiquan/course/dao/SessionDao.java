package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionDao extends CrudRepository<Session, Integer> {
    Optional<Session> findByCookie(String cookie);
}
