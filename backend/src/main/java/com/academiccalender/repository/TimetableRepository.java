package com.academiccalender.repository;

import com.academiccalender.model.Student;
import com.academiccalender.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable,Long> {
    // TimetableRepository.java
    List<Timetable> findByStudent(Student student);
    @Query("SELECT t FROM Timetable t WHERE t.student = :student AND t.Status = :status AND t.start_date <= :date")
    List<Timetable> findTimetableCustom(@Param("student") Student student,
                                        @Param("status") String status,
                                        @Param("date") Date date);

    Timetable findTimetableById(Long id);


}
