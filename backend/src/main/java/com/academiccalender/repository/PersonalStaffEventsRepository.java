package com.academiccalender.repository;

import com.academiccalender.model.PersonalStaffEvents;
import com.academiccalender.model.PersonalStudentEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalStaffEventsRepository extends JpaRepository<PersonalStaffEvents, Long> {


    List<PersonalStaffEvents> findAllByStaff_Id(Long studentId);
}
