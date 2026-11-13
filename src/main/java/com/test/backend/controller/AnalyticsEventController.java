package com.test.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.backend.dto.AnalyticsEventDTO;
import com.test.backend.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/analytics/events")
public class AnalyticsEventController {

    private final AnalyticsService analyticsService;

    public AnalyticsEventController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping
    public ResponseEntity<Void> record(
            @RequestBody AnalyticsEventDTO event,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        analyticsService.record(event, userAgent);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
