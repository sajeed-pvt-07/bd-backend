package com.test.backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AnalyticsSessionDTO(
        String sessionId,
        Instant startedAt,
        Instant lastSeen,
        Map<String, Long> pageViews,
        List<AnalyticsCountDTO> songs,
        List<AnalyticsCountDTO> selections,
        List<AnalyticsCountDTO> greetings,
        List<AnalyticsCountDTO> timelines,
        List<AnalyticsCountDTO> otherEvents,
        List<AnalyticsActivityDTO> activity
) {
}
