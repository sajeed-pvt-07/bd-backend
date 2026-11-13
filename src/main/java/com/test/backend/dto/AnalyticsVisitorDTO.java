package com.test.backend.dto;

import java.time.Instant;
import java.util.List;

public record AnalyticsVisitorDTO(
        String visitorId,
        String device,
        String browser,
        Instant lastSeen,
        List<AnalyticsSessionDTO> sessions
) {
}
