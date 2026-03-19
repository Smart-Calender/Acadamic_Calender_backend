package com.academiccalender.controller;

import com.academiccalender.dto.AttendanceResponse;
import com.academiccalender.model.Attendence;
import com.academiccalender.model.Student;
import com.academiccalender.model.Timetable;
import com.academiccalender.repository.AttendenceRepository;
import com.academiccalender.repository.StudentRepository;
import com.academiccalender.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller("/Attendence")
public class AttendenceController {

@Autowired
    StudentRepository studentRepository;
@Autowired
AttendenceRepository attendenceRepository;
@Autowired
    TimetableRepository  timetableRepository;

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

        List <Timetable> timetableList = timetableRepository.findByStudent(studentOpt.get());
        Set<Long> seenAttendanceIds = new HashSet<>();

        List<Timetable> uniqueTimetables = timetableList.stream()
                .filter(t -> {
                    Attendence a = t.getAttendence();
                    return a != null && seenAttendanceIds.add(a.getId());
                })
                .toList();

      if(timetableList.isEmpty()){
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body("Timetable not found");
      }
        List<AttendanceResponse> attendanceResponses = new ArrayList<>();

        for (Timetable timetable : uniqueTimetables) {

            AttendanceResponse response = new AttendanceResponse(
                    timetable.getCourse(),   // course
                    timetable.getAttendence().getAttendentedLectures(),     // present
                    timetable.getAttendence().getLecturesCount()            // total
            );

            attendanceResponses.add(response);
        }


        return ResponseEntity.ok(attendanceResponses);




    }


    @PutMapping("/updateAttendance/{id}")
    public ResponseEntity<?> updateAttendance(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        // 🔹 Get authenticated user
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
        Student student = studentOpt.get();

        Timetable timetable =timetableRepository.findTimetableById(id);



       Attendence attendence = timetable.getAttendence();


        // 🔹 Update counts based on status
        switch (status.toUpperCase()) {
            case "YES":
                attendence.setLecturesCount(attendence.getLecturesCount() + 1);
                attendence.setAttendentedLectures(attendence.getAttendentedLectures() + 1);
                break;
            case "NO":
                attendence.setLecturesCount(attendence.getLecturesCount() + 1);
                break;
            case "CANCELED":
                // nothing to increment
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid status");
        }

        // 🔹 Set status to CHECKED for all cases

        timetable.setStatus("CHECKED");
        timetableRepository.save(timetable);


        // 🔹 Save updated attendance
        attendenceRepository.save(attendence);

        return ResponseEntity.ok("Attendance updated successfully");
    }


}
