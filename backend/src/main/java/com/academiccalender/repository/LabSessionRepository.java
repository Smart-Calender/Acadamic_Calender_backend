package com.academiccalender.repository;

import com.academiccalender.model.LabSession;
import com.academiccalender.model.Labs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;


public interface LabSessionRepository extends JpaRepository<LabSession,Long> {
    boolean existsByLabAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Labs lab,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );


}
