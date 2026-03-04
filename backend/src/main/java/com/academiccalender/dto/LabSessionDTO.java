package com.academiccalender.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LabSessionDTO {
    private Long id;
    private Long courseId;
    private String practicalName;
    private Long labRoomID;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String sessionType;
    private String Description;
}