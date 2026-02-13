package com.academiccalender.controller;

import com.academiccalender.Util.JwtUtil;
import com.academiccalender.dto.User;
import com.academiccalender.model.Student;
import com.academiccalender.model.Staff;
import com.academiccalender.repository.StudentRepository;
import com.academiccalender.repository.StaffRepository;
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
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String email = user.getEmail();

        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        if (isStudentEmail(email)) {
            // Student registration
           try {
               Student student = new Student();
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
                staff.setName(user.getUsername());
                staff.setEmail(email);
                staff.setPassword(encodedPassword);
                staffRepository.save(staff);
                return ResponseEntity.ok(Map.of(
                        "message", "Student registered successfully!"
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
            return ResponseEntity.badRequest().body(Map.of("message", "Email or password is not set in the JSON request"));
        }

        String email = user.getEmail();
        String password = user.getPassword();
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email or password is null"));
        }
        String encodedPassword = passwordEncoder.encode(password);

        if (isStudentEmail(email)) {
            Student student = new Student();
            Student student1;
            student.setName(user.getUsername());
            student.setStudentEmail(email);
            student.setPassword(encodedPassword);

            try {
               if (studentRepository.findBystudentEmail(email).isPresent()) {

                   student1 = studentRepository.findBystudentEmail(email).get();
                   if(passwordEncoder.matches(user.getPassword(), student1.getPassword())) {

                       String token = JwtUtil.generateToken(student.getStudentEmail());
                       Map<String, Object> response = new HashMap<>();
                       response.put("token", token);
                       response.put("message", "Login successful!");
                       response.put("email",student1.getStudentEmail());
                       response.put("role","student");
                       response.put("semester",student1.getSemester());
                       response.put("name",student1.getName());
                       response.put("id",student1.getId());
                       System.out.println(response);
                       return ResponseEntity.ok(response);
                   }

               };




        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message","Failed",
                    "error",e.getMessage()
            ));
        }



        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Unauthorized access!"));



    }





    private boolean isStudentEmail(String email) {
        // Matches 20xxExxx@eng.jfn.ac.lk
        String pattern = "^20\\d{2}[Ee]\\d{3}@eng\\.jfn\\.ac\\.lk$";
        return email.matches(pattern);
    }
}
