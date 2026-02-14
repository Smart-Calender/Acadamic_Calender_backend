package com.academiccalender.repository;

import com.academiccalender.model.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogsRepository extends JpaRepository<UserLogs, Long> {

}
