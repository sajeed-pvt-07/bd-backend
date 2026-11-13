package com.test.backend.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "analytics_events")
public class AnalyticsEvent {

    @Id
    private String id;
    private String visitorId;
    private String sessionId;
    private String type;
    private String page;
    private String theme;
    private String browser;
    private String device;
    private String song;
    private String profileName;
    private Integer durationSeconds;
    private Instant recordedAt;
}
