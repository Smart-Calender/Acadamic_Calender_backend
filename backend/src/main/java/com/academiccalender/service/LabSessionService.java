package com.academiccalender.service;

import com.academiccalender.model.*;
import com.academiccalender.repository.LabSessionRepository;
import com.academiccalender.repository.PersonalStaffEventsRepository;
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

    @Autowired
    private PersonalStaffEventsRepository personalStaffEventsRepository;

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

    public void addlabtoStaffsPersonalEvent(LabSession labSession) {
        Course course= labSession.getCourse();

        if(course != null){
            List<Staff> staff=course.getStaff();
            for(Staff s: staff){
                PersonalStaffEvents personalStaffEvents = new PersonalStaffEvents();
                personalStaffEvents.setStaff(s);
                personalStaffEvents.setEvent(labSession.getSessionType()+" : "+labSession.getPracticalName());
                personalStaffEvents.setDate(labSession.getDate());
                personalStaffEvents.setTime(labSession.getStartTime());
                personalStaffEventsRepository.save(personalStaffEvents);
            }

        }

    }

    public void addlabtoInstructorPersonalEvent(LabSession labSession) {
        Staff staff= labSession.getInstructor();

        addtodatabase(labSession, staff);

    }

    public void addlabtoTechnicalOfficerPersonalEvent(LabSession labSession) {
        Staff staff= labSession.getTo();
        addtodatabase(labSession, staff);

    }



    private void addtodatabase(LabSession labSession, Staff staff) {
        if(staff != null){
                PersonalStaffEvents personalStaffEvents = new PersonalStaffEvents();
                personalStaffEvents.setStaff(staff);
                personalStaffEvents.setEvent(labSession.getSessionType()+" : "+labSession.getPracticalName());
                personalStaffEvents.setDate(labSession.getDate());
                personalStaffEvents.setTime(labSession.getStartTime());
                personalStaffEventsRepository.save(personalStaffEvents);


        }
    }



}
