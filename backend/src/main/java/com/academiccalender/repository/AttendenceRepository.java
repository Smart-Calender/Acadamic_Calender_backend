package com.academiccalender.repository;

import com.academiccalender.model.Attendence;
import com.academiccalender.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendenceRepository extends JpaRepository<Attendence, Long> {
    List<Attendence> getAttendenceByStudent(Optional<Student> student);
}
