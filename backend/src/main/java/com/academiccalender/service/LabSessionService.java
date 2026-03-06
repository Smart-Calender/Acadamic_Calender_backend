package com.academiccalender.service;

import com.academiccalender.model.Course;
import com.academiccalender.model.LabSession;
import com.academiccalender.model.PersonalStudentEvents;
import com.academiccalender.model.Student;
import com.academiccalender.repository.LabSessionRepository;
import com.academiccalender.repository.PersonalStudentEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabSessionService {
    @Autowired
    private LabSessionRepository labSessionRepository;

    @Autowired
    private PersonalStudentEventsRepository personalStudentEventsRepository;

    public void addlabtoStudentsPersonalEvent(LabSession labSession) {
       Course course= labSession.getCourse();

       if(course != null){
           List<Student> student=course.getStudents();
           for(Student s: student){
               PersonalStudentEvents personalStudentEvents = new PersonalStudentEvents();
               personalStudentEvents.setStudent(s);
               personalStudentEvents.setEvent(labSession.getSessionType()+" : "+labSession.getPracticalName());
               personalStudentEvents.setDate(labSession.getDate());
               personalStudentEvents.setTime(labSession.getStartTime());
               personalStudentEventsRepository.save(personalStudentEvents);
           }

       }

    }
}
