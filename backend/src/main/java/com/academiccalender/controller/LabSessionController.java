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
import com.academiccalender.service.LabSessionService;
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

    @Autowired
    LabSessionService labSessionService;


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
        if(dto.getSessionType().equals("LAB")){labSession.setTo(lab.getStaff());}
        labSession.setDescription(dto.getDescription());
        LabSession saved = labsessionRepository.save(labSession);

        if(!"LAB".equals(labSession.getSessionType()) &&
                !"LECTURES".equals(labSession.getSessionType())) {

            labSessionService.addlabtoStudentsPersonalEvent(labSession);
            labSessionService.addlabtoTechnicalOfficerPersonalEvent(labSession);
            labSessionService.addlabtoInstructorPersonalEvent(labSession);
            labSessionService.addlabtoStaffsPersonalEvent(labSession);
        }

//        if()


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

    // ─── UPDATE ───────────────────────────────────────────────
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLab(@PathVariable Long id, @RequestBody LabSessionDTO dto) {

        // 1️⃣ Find existing LabSession
        LabSession labSession = labsessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab session not found"));

        // 2️⃣ Validate Staff (updating user)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        Staff staff = staffRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        // 3️⃣ Optional: Check if user is instructor or admin
        if (!labSession.getInstructor().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to edit this lab session");
        }

        // 4️⃣ Validate Course
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        // 5️⃣ Validate Lab
        Labs lab = labsrepository.findById(dto.getLabRoomID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab not found"));

        // 6️⃣ Validate time
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            return ResponseEntity.badRequest()
                    .body("Start time must be before end time");
        }

        // 7️⃣ Check Lab Time Conflict (exclude current session)
        boolean conflict = labsessionRepository.existsByLabAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIdNot(
                lab,
                dto.getDate(),
                dto.getEndTime(),
                dto.getStartTime(),
                id
        );

        if (conflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Lab is already booked for this time");
        }

        // 8️⃣ Update fields
        labSession.setSessionType(dto.getSessionType());
        labSession.setDate(dto.getDate());
        labSession.setPracticalName(dto.getPracticalName());
        labSession.setStartTime(dto.getStartTime());
        labSession.setEndTime(dto.getEndTime());
        labSession.setCourse(course);
        labSession.setLab(lab);
        labSession.setTo(lab.getStaff());
        labSession.setDescription(dto.getDescription());

        LabSession updated = labsessionRepository.save(labSession);

        // 9️⃣ Add log
        userLogsService.addUserLogs(
                userId,
                "Lab Update",
                "Lab session updated for course '" + dto.getCourseId() +
                        "', practical '" + dto.getPracticalName() +
                        "', on date " + dto.getDate() +
                        " from " + dto.getStartTime() + " to " + dto.getEndTime() +
                        ". Requested to lab incharge staff ID: " + lab.getStaff().getId()
        );

        return ResponseEntity.ok(updated);
    }

    // ─── DELETE ───────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLab(@PathVariable Long id) {
         System.out.println("reaching");
        // 1️⃣ Find existing LabSession
        LabSession labSession = labsessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab session not found"));

        // 2️⃣ Validate user (only instructor or admin can delete)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();

        // 3️⃣ Delete session
        labsessionRepository.delete(labSession);

        // 4️⃣ Add log
        userLogsService.addUserLogs(
                userId,
                "Lab Deletion",
                "Lab session deleted for course '" + labSession.getCourse().getId() +
                        "', practical '" + labSession.getPracticalName() +
                        "', on date " + labSession.getDate() +
                        " from " + labSession.getStartTime() + " to " + labSession.getEndTime() +
                        ". Lab incharge staff ID: " + labSession.getLab().getStaff().getId()
        );

        return ResponseEntity.ok("Lab session deleted successfully");
    }
    // ─── FETCH LABS FOR LOGGED-IN INSTRUCTOR  ─────────────────
    @GetMapping("/my-labs")
    public ResponseEntity<?> getMyLabs() {
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

        // 1️⃣ Check if the instructor exists
        Staff instructor = staffRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));

        // 2️⃣ Fetch all lab sessions for this instructor
        List<LabSession> labs = labsessionRepository.findByInstructorId(userId);

        // 3️⃣ Handle empty case
        if (labs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No lab sessions found for this instructor");
        }

        // 4️⃣ Return result
        return ResponseEntity.ok(labs);
    }

    @GetMapping("/labs")
    public ResponseEntity<List<LabSession>> getAllLabs() {

        List<LabSession> labs = labsessionRepository.findAll();

        return ResponseEntity.ok(labs);
    }


    // ─── FETCH LABS FOR LOGGED-IN TECHNICAL OFFICER ─────────────────
    @GetMapping("/to-labs")
    public ResponseEntity<?> getToMyLabs() {
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

        // 1️⃣ Check if the instructor exists
        Staff instructor = staffRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));

        // 2️⃣ Fetch all lab sessions for this instructor
        List<LabSession> labs = labsessionRepository.findByToId(userId);

        // 3️⃣ Handle empty case
        if (labs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("No lab sessions found for this Technical Officer");
        }

        // 4️⃣ Return result
        return ResponseEntity.ok(labs);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectLab(@PathVariable Long id) {

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

        // Find lab session
        LabSession labSession = labsessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lab session not found"));

        // Optional: ensure the logged-in TO owns this lab
//        if (!labSession.getTo().getId().equals(userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("You are not allowed to reject this lab session");
//        }

        // Update status
        labSession.setStatus("REJECTED");
        labsessionRepository.save(labSession);

        userLogsService.addUserLogs(
                userId,
                "Lab Rejection",
                "Lab session deleted for course '" + labSession.getCourse().getId() +
                        "', practical '" + labSession.getPracticalName() +
                        "', on date " + labSession.getDate() +
                        " from " + labSession.getStartTime() + " to " + labSession.getEndTime() +
                        ". Lab incharge staff ID: " + labSession.getLab().getStaff().getId()
        );

        return ResponseEntity.ok("Lab session rejected successfully");
    }


    @PostMapping("approve/{id}")
    public ResponseEntity<?> approveLab(@PathVariable Long id) {

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

        // Find lab session
        LabSession labSession = labsessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lab session not found"));

//        // Optional: ensure the logged-in TO owns this lab
//        if (!labSession.getTo().getId().equals(userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("You are not allowed to approve this lab session");
//        }

        // Update status

        if(labSession.getStatus().equals("REJECTED") || labSession.getStatus().equals("PENDING")){
            labSessionService.addlabtoStudentsPersonalEvent(labSession);
            labSessionService.addlabtoTechnicalOfficerPersonalEvent(labSession);
            labSessionService.addlabtoInstructorPersonalEvent(labSession);
            labSessionService.addlabtoStaffsPersonalEvent(labSession);
        }


        labSession.setStatus("APPROVED");
        labsessionRepository.save(labSession);
        userLogsService.addUserLogs(
                userId,
                "Lab Approve",
                "Lab session Approved for course '" + labSession.getCourse().getId() +
                        "', practical '" + labSession.getPracticalName() +
                        "', on date " + labSession.getDate() +
                        " from " + labSession.getStartTime() + " to " + labSession.getEndTime() +
                        ". Lab incharge staff ID: " + labSession.getLab().getStaff().getId()
        );

        return ResponseEntity.ok("Lab session Approved successfully");

    }

}



