package com.academiccalender.dto;

import lombok.Data;

@Data
public class AttendanceResponse {
    private String course;
    private int present;
    private int total;

    public AttendanceResponse(String course, int attendentedLectures, int lecturesCount) {
        this.course = course;
        this.present = attendentedLectures;
        this.total = lecturesCount;
    }
}