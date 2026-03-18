package com.academiccalender.repository;

import com.academiccalender.model.Student;
import com.academiccalender.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable,Long> {
    // TimetableRepository.java
    List<Timetable> findByStudent(Student student);


}
