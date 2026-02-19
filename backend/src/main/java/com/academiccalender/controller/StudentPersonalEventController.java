package com.academiccalender.controller;

import com.academiccalender.dto.StudentPersonalEventDTO;
import com.academiccalender.model.PersonalStudentEvents;
import com.academiccalender.model.Student;
import com.academiccalender.repository.PersonalStudentEventsRepository;
import com.academiccalender.repository.StudentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/studentpersonalevent")

public class StudentPersonalEventController {
 @Autowired
 PersonalStudentEventsRepository personalStudentEventsRepository;
 @Autowired
 StudentRepository studentRepository;




    @GetMapping("/getEvents")
    public ResponseEntity<?> getEvents() {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal()==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        Long userId;
        try {
            userId = (Long) auth.getPrincipal();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("Invalid principal type in authentication");
        }

        var events = personalStudentEventsRepository.findAllByStudent_Id(userId);

        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(events);
    }

    @PostMapping("/addpersonalStudentEvents")
    public ResponseEntity<?> addPersonalStudentEvents(@RequestBody StudentPersonalEventDTO dto) {

        if (dto == null) {
            return new ResponseEntity<>("Request body is empty", HttpStatus.BAD_REQUEST);
        }
        System.out.println(dto.getEvent());
        System.out.println(dto.getStudentID());
        if (dto.getStudentID() == null) {
            return new ResponseEntity<>("StudentId is null", HttpStatus.BAD_REQUEST);
        }
        // Find student by ID
        Optional<Student> studentOpt = studentRepository.findById(dto.getStudentID());

        if (studentOpt.isEmpty()) {
            // Student does not exist
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        Student student = studentOpt.get(); // Get actual Student object
try {
    // Create PersonalStudentEvents object
    PersonalStudentEvents personalEvent = new PersonalStudentEvents();
    personalEvent.setStudent(student);
    personalEvent.setEvent(dto.getEvent());
    personalEvent.setDate(dto.getDate());
    personalEvent.setTime(dto.getTime());
    // Save to DB
    personalStudentEventsRepository.save(personalEvent);

    // Return the saved object with 201 CREATED
    return new ResponseEntity<>(personalEvent, HttpStatus.CREATED);
}
catch(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                    "message", "failed to add the event",
                    "error", e.getMessage()
            ));
}

    }


    @DeleteMapping("/deletepersonalStudentEvents/{id}")
    public ResponseEntity<?> deletePersonalStudentEvents(@PathVariable Long id) {

        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (personalStudentEventsRepository.existsById(id)) {
            personalStudentEventsRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }



        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateEvent")
    public ResponseEntity<?> updateEvent(@RequestBody PersonalStudentEvents updatedData) {

        PersonalStudentEvents personalEvent = new PersonalStudentEvents();
try {
    if ( updatedData.getEvent() == null || updatedData.getDate() == null || updatedData.getTime() == null) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    if (personalStudentEventsRepository.findById(updatedData.getId()).isPresent()) {
        personalEvent=personalStudentEventsRepository.findById(updatedData.getId()).get();
        updatedData.setStudent(personalEvent.getStudent());
        personalStudentEventsRepository.save(updatedData);
        return new ResponseEntity<>(personalEvent, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}
catch(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                    "message", "Updating failed",
                    "error", e.getMessage()
            ));
}
    }


}
