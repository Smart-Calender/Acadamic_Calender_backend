package com.academiccalender.repository;

import com.academiccalender.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find a student by email (used for login)
    Optional<Student> findBystudentEmail(String email);
}
