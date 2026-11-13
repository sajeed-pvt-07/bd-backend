package com.test.backend.dto;

import lombok.Data;

@Data
public class AnalyticsEventDTO {
    private String visitorId;
    private String sessionId;
    private String type;
    private String page;
    private String theme;
    private String song;
    private String profileName;
    private Integer durationSeconds;
}
