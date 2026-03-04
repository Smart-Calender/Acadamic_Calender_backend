package com.academiccalender.controller;

import com.academiccalender.dto.LabSessionDTO;
import com.academiccalender.model.Course;
import com.academiccalender.model.LabSession;
import com.academiccalender.model.Labs;
import com.academiccalender.model.Staff;
import com.academiccalender.repository.CourseRepository;
import com.academiccalender.repository.LabRepository;
import com.academiccalender.repository.LabSessionRepository;
import com.academiccalender.repository.StaffRepository;
import com.academiccalender.service.UserLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/labsession")
public class LabSessionController {

    @Autowired
    LabSessionRepository labsessionRepository;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    LabRepository labsrepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserLogsService  userLogsService;


    // ─── CREATE ───────────────────────────────────────────────
    @PostMapping("/add")
    public ResponseEntity<?> addLab(@RequestBody LabSessionDTO dto) {

        // 1️⃣ Validate authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        Long userId;
        try {
            userId = (Long) auth.getPrincipal();
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid authentication principal");
        }

        // 2️⃣ Validate Staff
        Staff staff = staffRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        // 3️⃣ Validate Course
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        // 4️⃣ Validate Lab
        Labs lab = labsrepository.findById(dto.getLabRoomID())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab not found"));

        // 5️⃣ Validate time
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            return ResponseEntity.badRequest()
                    .body("Start time must be before end time");
        }

        // 6️⃣ Check Lab Time Conflict
        boolean conflict = labsessionRepository
                .existsByLabAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        lab,
                        dto.getDate(),
                        dto.getEndTime(),
                        dto.getStartTime()
                );

        if (conflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Lab is already booked for this time");
        }

        // 7️⃣ Create LabSession
        LabSession labSession = new LabSession();
        labSession.setSessionType(dto.getSessionType());
        labSession.setDate(dto.getDate());
        labSession.setPracticalName(dto.getPracticalName());
        labSession.setStartTime(dto.getStartTime());
        labSession.setEndTime(dto.getEndTime());
        labSession.setInstructor(staff);
        labSession.setCourse(course);
        labSession.setLab(lab);
        labSession.setStatus("PENDING");
        labSession.setTo(lab.getStaff());
        labSession.setDescription(dto.getDescription());

        LabSession saved = labsessionRepository.save(labSession);
        userLogsService.addUserLogs(
                userId,
                "Lab Creation",
                "Lab requested successfully for course '" + dto.getCourseId() +
                        "', practical '" + dto.getPracticalName() +
                        "', on date " + dto.getDate() +
                        " from " + dto.getStartTime() + " to " + dto.getEndTime() +
                        ". Requested to lab incharge staff ID: " + lab.getStaff().getId()
        );
        // 8️⃣ Return structured response
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }
}


