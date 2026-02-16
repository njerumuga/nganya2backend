package com.nganyaexperience.backend.dto;

import lombok.Data;

@Data
public class AdminEventRequest {
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
    private String status;
}
