package com.academiccalender.controller;

import com.academiccalender.Util.JwtUtil;
import com.academiccalender.dto.User;
import com.academiccalender.model.Student;
import com.academiccalender.model.Staff;
import com.academiccalender.repository.StudentRepository;
import com.academiccalender.repository.StaffRepository;
import com.academiccalender.service.UserLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserLogsService  userLogsService;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String email = user.getEmail();

        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        if (isStudentEmail(email)) {
            // Student registration
           try {
               Student student = new Student();
               Student student1;
               student.setName(user.getUsername());
               student.setStudentEmail(email);
               student.setPassword(encodedPassword);
               studentRepository.save(student);


               return ResponseEntity.ok(Map.of(
                       "message", "Student registered successfully!",
                       "email", email
               ));
           }
         catch (Exception e) {
            // Return the exception message to the client
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Registration failed!",
                            "error", e.getMessage()
                    ));
        }
        } else {
            // Staff registration
            try {
                Staff staff = new Staff();
                Staff staff1;
                staff.setName(user.getUsername());
                staff.setEmail(email);
                staff.setPassword(encodedPassword);
                staffRepository.save(staff);
                if(staffRepository.findByEmail(email).isPresent()){
                    staff1=staffRepository.findByEmail(email).get();
                    long id= staff1.getId();
                    userLogsService.addUserLogs(id,"Registration","User Registered Successfully");
                };
                return ResponseEntity.ok(Map.of(
                        "message", "Staff registered successfully!"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message","Regestration Failed",
                        "error",e.getMessage()

                ));
            }
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email or password is missing"));
        }

        String email = user.getEmail();
        String password = user.getPassword();

        try {

            // ======================
            // STUDENT LOGIN
            // ======================
            Optional<Student> studentOptional = studentRepository.findByStudentEmail(email);

            if (studentOptional.isPresent()) {

                Student student = studentOptional.get();

                if (passwordEncoder.matches(password, student.getPassword())) {

                    String token = jwtUtil.generateToken(student.getId(),student.getStudentEmail(),"student");

                    userLogsService.addUserLogs(
                            student.getId(),
                            "Login",
                            "Student Logged Successfully"
                    );

                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "message", "Login successful!",
                            "role", "student",
                            "email", student.getStudentEmail(),
                            "semester", student.getSemester(),
                            "name", student.getName(),
                            "id", student.getId()
                    ));
                }
            }

            // ======================
            // STAFF LOGIN
            // ======================
            Optional<Staff> staffOptional = staffRepository.findByEmail(email);

            if (staffOptional.isPresent()) {

                Staff staff = staffOptional.get();

                if (passwordEncoder.matches(password, staff.getPassword())) {

                    String token = jwtUtil.generateToken(staff.getId(),staff.getEmail(),staff.getRole());

                    userLogsService.addUserLogs(
                            staff.getId(),
                            "Login",
                            "Staff Logged Successfully"
                    );

                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "message", "Login successful!",
                            "role", staff.getRole() != null ? staff.getRole() : "Not Assigned",
                            "email", staff.getEmail(),
                            "name", staff.getName(),
                            "id", staff.getId()
                    ));
                }
            }

            // ======================
            // IF NOT FOUND
            // ======================

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Login Failed",
                            "error", e.getMessage()
                    ));
        }
    }

    @PostMapping("/reset-password")
        public ResponseEntity<?> resetPassword(@RequestBody User user) {

        if(user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email or password is missing"));
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        Optional<Student> studentOptional = studentRepository.findByStudentEmail(user.getEmail());
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setPassword(encodedPassword);
            studentRepository.save(student);
            return ResponseEntity.ok(Map.of());
        }
        Optional<Staff> staffOptional = staffRepository.findByEmail(user.getEmail());
        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            staff.setPassword(encodedPassword);
            staffRepository.save(staff);
            return ResponseEntity.ok(Map.of());
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Reseting Password Failed"));
        }

    private boolean isStudentEmail(String email) {
        // Matches 20xxExxx@eng.jfn.ac.lk
        String pattern = "^20\\d{2}[Ee]\\d{3}@eng\\.jfn\\.ac\\.lk$";
        return email.matches(pattern);
    }
}
