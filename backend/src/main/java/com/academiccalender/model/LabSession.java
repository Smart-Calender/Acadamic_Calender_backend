package com.academiccalender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter
@Setter

public class LabSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    private String PracticalName;

    @JoinColumn(name="lab_id")
    @ManyToOne
    private Labs lab;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String sessionType;

    @JoinColumn(name="instructor_id")
    @ManyToOne
    private Staff instructor;


    @JoinColumn(name="TO_id")
    @ManyToOne
    private Staff to;

    private String status="Pending";
    private String Description;





}