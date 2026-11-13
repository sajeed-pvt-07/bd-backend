package com.test.backend.dto;

import java.time.Instant;

public record AnalyticsActivityDTO(
        Instant recordedAt,
        String type,
        String page,
        String theme,
        String song,
        String profileName,
        Integer durationSeconds
) {
}
