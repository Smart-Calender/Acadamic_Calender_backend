package com.academiccalender.controller;

import com.academiccalender.dto.AttendanceResponse;
import com.academiccalender.model.Attendence;
import com.academiccalender.model.Student;
import com.academiccalender.repository.AttendenceRepository;
import com.academiccalender.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller("/Attendence")
public class AttendenceController {

@Autowired
    StudentRepository studentRepository;
@Autowired
AttendenceRepository attendenceRepository;


    @GetMapping("/getAttendnce")
   public ResponseEntity<?> getAttendence() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 🔹 Get student
        Optional<Student> studentOpt = studentRepository.findById(userId);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }



        // 🔹 Fetch attendance list
        List<Attendence> attendenceList =
                attendenceRepository.getAttendenceByStudent(studentOpt);

        if (attendenceList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No attendance records found");
        }
        List<AttendanceResponse> attendanceResponses = new ArrayList<>();

        for (Attendence attendence : attendenceList) {

            AttendanceResponse response = new AttendanceResponse(
                    attendence.getTimetable().getCourse(),   // course
                    attendence.getAttendentedLectures(),     // present
                    attendence.getLecturesCount()            // total
            );

            attendanceResponses.add(response);
        }


        return ResponseEntity.ok(attendanceResponses);
    }



}
