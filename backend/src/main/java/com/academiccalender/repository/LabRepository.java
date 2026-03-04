package com.academiccalender.repository;

import com.academiccalender.model.Labs;
import com.academiccalender.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepository extends JpaRepository<Labs, Long> {



}
