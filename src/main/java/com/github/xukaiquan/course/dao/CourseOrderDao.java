package com.github.xukaiquan.course.dao;

import com.github.xukaiquan.course.model.CourseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseOrderDao extends JpaRepository<CourseOrder, Integer> {
    Optional<CourseOrder> findByCourseIdAndUserId(Integer courseId, Integer userId);
}