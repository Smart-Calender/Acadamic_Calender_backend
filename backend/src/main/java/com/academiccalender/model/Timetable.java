package com.academiccalender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="Student_Id")
    @ManyToOne
    @JsonIgnore
    private Student student;

    private String Status;

    private String course;
    private Date start_date;
    private Date end_date;
    private String start_time;
    private String end_time;
    private String day;

    @JoinColumn(name="attendence_id")
    @ManyToOne
    @JsonIgnore
    private Attendence attendence;
}
