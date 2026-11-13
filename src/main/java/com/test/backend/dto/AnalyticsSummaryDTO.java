package com.test.backend.dto;

import java.util.List;

public record AnalyticsSummaryDTO(
        int days,
        long uniqueVisitors,
        long sessions,
        long pageViews,
        long averagePageDurationSeconds,
        List<AnalyticsCountDTO> topPages,
        List<AnalyticsCountDTO> themes,
        List<AnalyticsCountDTO> browsers,
        List<AnalyticsCountDTO> devices,
        List<AnalyticsCountDTO> topSongs,
        List<AnalyticsDailyCountDTO> dailyPageViews,
        List<AnalyticsCountDTO> events,
        List<AnalyticsVisitorDTO> visitorDetails
) {
}
