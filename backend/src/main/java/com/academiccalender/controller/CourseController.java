package com.academiccalender.controller;

import com.academiccalender.dto.PersonalEventDTO;
import com.academiccalender.model.Course;
import com.academiccalender.model.Student;
import com.academiccalender.repository.CourseRepository;
import com.academiccalender.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Course>> findAll() {

        List<Course> courses = courseRepository.findAll();

        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if no courses found
        }

        return ResponseEntity.ok(courses); // 200 OK with list of courses
    }

    @GetMapping ("/enrolled")
    public ResponseEntity<List<?>> enrolled() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long id = (Long) auth.getPrincipal();

            List<Course> enrolledCourses = courseRepository.findAllEnrolledCoursesByStudent(id);
            if (enrolledCourses.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonList("No enrolled courses found for this student"));
            }
            return ResponseEntity.ok(enrolledCourses);

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("Make sure You have sent the Tokern"));
        }
    }

   @PostMapping("/enroll/{id}")
    public ResponseEntity<?> enroll(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long studentId = (Long) auth.getPrincipal();

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList("Student with this id does not exist"));
            }

            Course course = courseRepository.findById(id).orElse(null);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList("Course with this id does not exist"));
            }
            course.getStudents().add(student);
            courseRepository.save(course);
            return ResponseEntity.ok(course);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(e.getMessage()));
        }
    }


}
