package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role,Integer> {
}
