package com.github.xukaiquan.course.dao;


import com.github.xukaiquan.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseDao extends JpaRepository<Course, Integer> {
}