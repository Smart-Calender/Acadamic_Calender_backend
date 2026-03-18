package com.academiccalender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Attendence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Student student;

    private int LecturesCount;

    private int attendentedLectures;


    @JoinColumn(name="Lectures_id")
    @OneToOne
    @JsonIgnore
    private Timetable timetable;

}
