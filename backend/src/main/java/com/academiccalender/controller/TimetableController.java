package com.academiccalender.controller;
import java.util.*;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

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

// Keep only unique courses
        Map<String, Timetable> uniqueCourses = new LinkedHashMap<>();
        for (Timetable t : timetableList) {
            uniqueCourses.putIfAbsent(t.getCourse(), t); // first occurrence only
        }

        return ResponseEntity.ok(new ArrayList<>(uniqueCourses.values()));
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
        timetable.setStatus("NOT_CHECKED");


        Attendence attendence = new Attendence();
        attendence.setStudent(student);
        attendence.setAttendentedLectures(0);
        attendence.setLecturesCount(0);
        Attendence Saveattendence =attendenceRepository.save(attendence);

        timetable.setAttendence(Saveattendence);



        Timetable saved = timetableRepository.save(timetable);

        saveWeeklyTimetable(timetable,student,Saveattendence);






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


    public void saveWeeklyTimetable(Timetable timetable,Student student,Attendence attendence) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(timetable.getStart_date());

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(timetable.getEnd_date());

        // Convert day string to Calendar.DAY_OF_WEEK
        int dayOfWeek = switch (timetable.getDay().toLowerCase()) {
            case "sun" -> Calendar.SUNDAY;
            case "mon" -> Calendar.MONDAY;
            case "tue" -> Calendar.TUESDAY;
            case "wed" -> Calendar.WEDNESDAY;
            case "thu" -> Calendar.THURSDAY;
            case "fri" -> Calendar.FRIDAY;
            case "sat" -> Calendar.SATURDAY;
            default -> throw new IllegalArgumentException("Invalid day: " + timetable.getDay());
        };

        // Move startCal to the first occurrence of the desired day
        while (startCal.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Skip the first week
        startCal.add(Calendar.WEEK_OF_YEAR, 1);

        // Loop through all weeks until end date
        Calendar current = (Calendar) startCal.clone();
        while (!current.after(endCal)) {
            Timetable t = new Timetable();
            t.setCourse(timetable.getCourse());
            t.setStart_date(current.getTime());
            t.setEnd_date(current.getTime()); // or keep original endDate if needed
            t.setStart_time(timetable.getStart_time());
            t.setEnd_time(timetable.getEnd_time());
            t.setDay(timetable.getDay());
            t.setStatus("NOT_CHECKED");
            t.setStudent(student);
            t.setAttendence(attendence);

            timetableRepository.save(t);

            current.add(Calendar.WEEK_OF_YEAR, 1); // move to next week
        }
    }

    @GetMapping("trackAttendence")
    public ResponseEntity<?> trackAttendence() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();


        Optional<Student> studentOpt = studentRepository.findById(userId);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }


        Student student = studentOpt.get();
        // Fetch timetable entries up to today and NOT_CHECKED
        Date today = new Date();
        List<Timetable> timetableList = timetableRepository
                .findTimetableCustom(student, "NOT_CHECKED", today);
        if (timetableList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No timetable entries found");

        }
        return ResponseEntity.ok(timetableList);

    }

}
