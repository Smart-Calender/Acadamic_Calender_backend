package com.academiccalender.repository;

import com.academiccalender.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Override
    List<Course> findAll();

}
