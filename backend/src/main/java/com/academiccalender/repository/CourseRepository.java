package com.academiccalender.repository;

import com.academiccalender.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Override
    List<Course> findAll();

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :id")
    List<Course> findAllEnrolledCoursesByStudent(@Param("id") Long id);

    List<Course> findByStaffId(Long staffId);




}
