package com.academiccalender.repository;

import com.academiccalender.model.PersonalStudentEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalStudentEventsRepository extends JpaRepository<PersonalStudentEvents, Long> {
    List<PersonalStudentEvents> findAllByStudent_Id(Long studentId);
    List<PersonalStudentEvents> findByStudent_Id(Long studentId);


}
