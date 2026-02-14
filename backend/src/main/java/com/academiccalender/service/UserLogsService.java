package com.academiccalender.service;

import com.academiccalender.model.UserLogs;
import com.academiccalender.repository.UserLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class UserLogsService {

    @Autowired
    UserLogsRepository userLogsRepository;

   public void addUserLogs (Long userId, String eventType, String message){
       UserLogs userLogs = new UserLogs();

       userLogs.setEventType(eventType);
       userLogs.setMessage(message);
       userLogs.setUserID(userId);
       userLogs.setDate(LocalDate.now().toString());
       userLogs.setTime(LocalTime.now().toString());

       userLogsRepository.save(userLogs);



   }
}
