package com.academiccalender.model;

import jakarta.persistence.*;

@Entity
public class HOD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="Department_id")
    @OneToOne
    private Department department;

    @JoinColumn(name="Staff_id")
    @OneToOne
    private Staff staff;
}
