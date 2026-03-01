package com.academiccalender.controller;

import com.academiccalender.dto.PersonalEventDTO;
import com.academiccalender.model.*;
import com.academiccalender.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/personalevent")
public class PersonalEventController {

    @Autowired
    private PersonalStudentEventsRepository personalStudentEventsRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PersonalStaffEventsRepository staffPersonalEventRepository;

    // =========================
    // GET EVENTS (ROLE BASED)
    // =========================
    @GetMapping("/getEvents")
    public ResponseEntity<?> getEvents() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        String role = auth.getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority();
         System.out.println("Testing 1 "+role);
        // STAFF SIDE
        if (isStaffRole(role)) {
            var events = staffPersonalEventRepository.findAllByStaff_Id(userId);
            return ResponseEntity.ok(events);
        }

        // STUDENT SIDE
        var events = personalStudentEventsRepository.findAllByStudent_Id(userId);
        return ResponseEntity.ok(events);
    }

    // =========================
    // ADD EVENT (ROLE BASED)
    // =========================
    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@RequestBody PersonalEventDTO dto) {

        if (dto == null || dto.getEvent() == null ||
                dto.getDate() == null || dto.getTime() == null) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        System.out.println("Testing reaching the 2 "+dto.getEvent());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        String role = auth.getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority();


        try {

            // STAFF SIDE
            if (isStaffRole(role)) {

                Optional<Staff> staffOpt = staffRepository.findById(userId);
                if (staffOpt.isEmpty())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");

                PersonalStaffEvents event = new PersonalStaffEvents();
                event.setStaff(staffOpt.get());
                event.setEvent(dto.getEvent());
                event.setDate(dto.getDate());
                event.setTime(dto.getTime());


                staffPersonalEventRepository.save(event);
                return ResponseEntity.status(HttpStatus.CREATED).body(event);
            }

            // STUDENT SIDE
            Optional<Student> studentOpt = studentRepository.findById(userId);
            System.out.println("Saving 55 my name "+studentOpt);
            if (studentOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

            PersonalStudentEvents event = new PersonalStudentEvents();
            event.setStudent(studentOpt.get());
            event.setEvent(dto.getEvent());
            event.setDate(dto.getDate());
            event.setTime(dto.getTime());



            personalStudentEventsRepository.save(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Failed to add event",
                            "error", e.getMessage()
                    ));
        }
    }

    // =========================
    // DELETE EVENT (SECURE)
    // =========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();

        if (isStaffRole(role)) {

            Optional<PersonalStaffEvents> eventOpt =
                    staffPersonalEventRepository.findById(id);

            if (eventOpt.isEmpty())
                return ResponseEntity.notFound().build();

            if (!eventOpt.get().getStaff().getId().equals(userId))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            staffPersonalEventRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully");
        }

        Optional<PersonalStudentEvents> eventOpt =
                personalStudentEventsRepository.findById(id);

        if (eventOpt.isEmpty())
            return ResponseEntity.notFound().build();

        if (!eventOpt.get().getStudent().getId().equals(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        personalStudentEventsRepository.deleteById(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // =========================
    // UPDATE EVENT (SECURE)
    // =========================
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id,
                                         @RequestBody PersonalEventDTO dto) {

        if (dto == null || dto.getEvent() == null ||
                dto.getDate() == null || dto.getTime() == null) {
            return ResponseEntity.badRequest().body("Invalid data");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        String role = auth.getAuthorities().stream().findFirst().get().getAuthority();

        if (isStaffRole(role)) {

            Optional<PersonalStaffEvents> eventOpt =
                    staffPersonalEventRepository.findById(id);

            if (eventOpt.isEmpty())
                return ResponseEntity.notFound().build();

            PersonalStaffEvents event = eventOpt.get();

            if (!event.getStaff().getId().equals(userId))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            event.setEvent(dto.getEvent());
            event.setDate(dto.getDate());
            event.setTime(dto.getTime());

            staffPersonalEventRepository.save(event);
            return ResponseEntity.ok(event);
        }

        Optional<PersonalStudentEvents> eventOpt =
                personalStudentEventsRepository.findById(id);

        if (eventOpt.isEmpty())
            return ResponseEntity.notFound().build();

        PersonalStudentEvents event = eventOpt.get();

        if (!event.getStudent().getId().equals(userId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        event.setEvent(dto.getEvent());
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());

        personalStudentEventsRepository.save(event);
        return ResponseEntity.ok(event);
    }

    // =========================
    // ROLE CHECK HELPER
    // =========================
    private boolean isStaffRole(String role) {
        return role.equals("ROLE_STAFF") ||
                role.equals("ROLE_LECTURER") ||
                role.equals("ROLE_TO") ||
                role.equals("ROLE_INSTRUCTOR") ||
                role.equals("ROLE_HOD");
    }
}