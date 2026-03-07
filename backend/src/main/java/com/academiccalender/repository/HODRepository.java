package com.academiccalender.repository;

import com.academiccalender.model.Department;
import com.academiccalender.model.HOD;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HODRepository extends JpaRepository<HOD, Long> {

    HOD findByDepartment(Department department);
}
