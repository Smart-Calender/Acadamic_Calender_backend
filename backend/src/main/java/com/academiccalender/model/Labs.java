package com.academiccalender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Labs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String labName;

    @JoinColumn(name="Department_id")
    @OneToOne
    private Department department;

    @JoinColumn(name="LabIncharge")
    @OneToOne
    private Staff staff;
}
