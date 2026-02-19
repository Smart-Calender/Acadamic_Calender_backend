package com.academiccalender.repository;

import com.academiccalender.model.PersonalStaffEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalStaffEventsRepository extends JpaRepository<PersonalStaffEvents, Long> {

}
