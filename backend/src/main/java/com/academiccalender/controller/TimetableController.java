package com.academiccalender.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller("/Timetable")
public class TimetableController {
    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendenceRepository attendenceRepository;

    @GetMapping("/getTimetable")
    public ResponseEntity<?> getTimeTable() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        // 🔹 Get student
        Optional<Student> studentOpt = studentRepository.findById(userId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }


        Student student = studentOpt.get();

        // 🔹 Get all timetables for this student
        List<Timetable> timetableList = timetableRepository.findByStudent(student);
        if (timetableList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Timetable not found");
        }

        return ResponseEntity.ok(timetableList);
    }
    @PostMapping("/addTimetable")
    public ResponseEntity<?> addTimetable(@RequestBody Timetable timetable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        Optional<Student> studentOpt = studentRepository.findById(userId);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }

        Student student = studentOpt.get();


        timetable.setStudent(student);

        Timetable saved = timetableRepository.save(timetable);

        Attendence attendence = new Attendence();
        attendence.setStudent(student);
        attendence.setAttendentedLectures(0);
        attendence.setLecturesCount(0);
        attendence.setTimetable(saved);
        attendenceRepository.save(attendence);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTimetable(
            @PathVariable Long id,
            @RequestBody Timetable updated) {

        Optional<Timetable> timetableOpt = timetableRepository.findById(id);

        if (timetableOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Timetable entry not found");
        }

        Timetable existing = timetableOpt.get();

        // 🔒 Optional: check ownership
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        if (!existing.getStudent().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not allowed");
        }

        // 🔥 Update fields
        existing.setCourse(updated.getCourse());
        existing.setDay(updated.getDay());
        existing.setStart_time(updated.getStart_time());
        existing.setEnd_time(updated.getEnd_time());
        existing.setStart_date(updated.getStart_date());
        existing.setEnd_date(updated.getEnd_date());

        Timetable saved = timetableRepository.save(existing);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTimetable(@PathVariable Long id) {

        Optional<Timetable> timetableOpt = timetableRepository.findById(id);

        if (timetableOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Timetable entry not found");
        }

        Timetable timetable = timetableOpt.get();

        // 🔒 Ownership check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        if (!timetable.getStudent().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not allowed");
        }

        timetableRepository.delete(timetable);

        return ResponseEntity.ok("Deleted successfully");
    }

}
