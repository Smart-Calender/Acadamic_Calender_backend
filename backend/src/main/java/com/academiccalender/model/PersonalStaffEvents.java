package com.academiccalender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class PersonalStaffEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn (name ="staff_id")
    @ManyToOne
    @JsonIgnore
    private Staff staff;

    private String event;
    private LocalDate date;
    private LocalTime time;



}
