package com.academiccalender.controller;

import com.academiccalender.model.Course;
import com.academiccalender.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Course>> findAll() {

        List<Course> courses = courseRepository.findAll();

        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if no courses found
        }

        return ResponseEntity.ok(courses); // 200 OK with list of courses
    }

   /* @GetMapping ("/enrolled")
    public ResponseEntity<List<Course>> enrolled() {}*/

}
