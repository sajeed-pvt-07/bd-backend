package com.test.backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.test.backend.dto.AnalyticsCountDTO;
import com.test.backend.dto.AnalyticsDailyCountDTO;
import com.test.backend.dto.AnalyticsActivityDTO;
import com.test.backend.dto.AnalyticsEventDTO;
import com.test.backend.dto.AnalyticsSummaryDTO;
import com.test.backend.dto.AnalyticsSessionDTO;
import com.test.backend.dto.AnalyticsVisitorDTO;
import com.test.backend.entity.AnalyticsEvent;
import com.test.backend.repository.AnalyticsEventRepository;

@Service
public class AnalyticsService {

    private static final Set<String> EVENT_TYPES = Set.of(
            "SESSION_START", "PAGE_VIEW", "PAGE_LEAVE", "THEME_SET", "THEME_CHANGE",
            "SONG_PLAY", "SONG_PAUSE", "SONG_END", "PROFILE_SELECT",
            "GREETING_CONTINUE", "BIRTHDAY_BALLOON_POP", "BIRTHDAY_COMPLETE",
            "OPEN_WHEN_NOTE_OPEN", "JOURNEY_REPLAY", "TOUR_START", "TOUR_SKIP"
    );
    private static final Set<String> PAGES = Set.of(
            "home", "birthday-celebration", "user-selection", "greeting", "timeline", "thank-you",
            "open-when"
    );

    private final AnalyticsEventRepository repository;

    public AnalyticsService(AnalyticsEventRepository repository) {
        this.repository = repository;
    }

    public void record(AnalyticsEventDTO dto, String userAgent) {
        validate(dto);

        AnalyticsEvent event = new AnalyticsEvent();
        event.setVisitorId(dto.getVisitorId());
        event.setSessionId(dto.getSessionId());
        event.setType(dto.getType());
        event.setPage(dto.getPage());
        event.setTheme(dto.getTheme());
        event.setBrowser(detectBrowser(userAgent));
        event.setDevice(detectDevice(userAgent));
        event.setSong(dto.getSong());
        event.setProfileName(dto.getProfileName());
        event.setDurationSeconds(dto.getDurationSeconds());
        event.setRecordedAt(Instant.now());
        repository.save(event);
    }

    public AnalyticsSummaryDTO getSummary(int days) {
        if (days < 0 || days > 3650) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Days must be between 1 and 3650, or 0 for all time");
        }

        List<AnalyticsEvent> events = days == 0
                ? repository.findAll()
                : repository.findByRecordedAtAfter(Instant.now().minusSeconds(days * 24L * 60L * 60L));
        Set<String> visitors = new HashSet<>();
        Set<String> sessions = new HashSet<>();
        Map<String, Set<String>> themes = new HashMap<>();
        Map<String, Set<String>> browsers = new HashMap<>();
        Map<String, Set<String>> devices = new HashMap<>();
        Map<String, Long> pages = new HashMap<>();
        Map<String, Long> songs = new HashMap<>();
        Map<String, Long> eventCounts = new HashMap<>();
        Map<LocalDate, Long> dailyPageViews = new TreeMap<>();
        long totalDwellSeconds = 0;
        long pageLeaves = 0;
        long pageViews = 0;

        for (AnalyticsEvent event : events) {
            visitors.add(event.getVisitorId());
            sessions.add(event.getSessionId());
            eventCounts.merge(event.getType(), 1L, Long::sum);
            addVisitorCount(themes, event.getTheme(), event.getVisitorId());
            addVisitorCount(browsers, event.getBrowser(), event.getVisitorId());
            addVisitorCount(devices, event.getDevice(), event.getVisitorId());

            if ("PAGE_VIEW".equals(event.getType())) {
                pageViews++;
                pages.merge(event.getPage(), 1L, Long::sum);
                dailyPageViews.merge(event.getRecordedAt().atZone(ZoneOffset.UTC).toLocalDate(), 1L, Long::sum);
            }
            if ("PAGE_LEAVE".equals(event.getType()) && event.getDurationSeconds() != null) {
                totalDwellSeconds += event.getDurationSeconds();
                pageLeaves++;
            }
            if ("SONG_PLAY".equals(event.getType()) && event.getSong() != null) {
                songs.merge(event.getSong(), 1L, Long::sum);
            }
        }

        List<AnalyticsDailyCountDTO> daily = dailyPageViews.entrySet().stream()
                .map(entry -> new AnalyticsDailyCountDTO(entry.getKey().toString(), entry.getValue()))
                .toList();

        return new AnalyticsSummaryDTO(
                days,
                visitors.size(),
                sessions.size(),
                pageViews,
                pageLeaves == 0 ? 0 : totalDwellSeconds / pageLeaves,
                toCountList(pages, 10),
                toVisitorCountList(themes),
                toVisitorCountList(browsers),
                toVisitorCountList(devices),
                toCountList(songs, 10),
                daily,
                toCountList(eventCounts, 20),
                toVisitorDetails(events)
        );
    }

    private List<AnalyticsVisitorDTO> toVisitorDetails(List<AnalyticsEvent> events) {
        Map<String, List<AnalyticsEvent>> visitorGroups = events.stream()
                .collect(Collectors.groupingBy(AnalyticsEvent::getVisitorId));

        return visitorGroups.entrySet().stream()
                .map(entry -> {
                    List<AnalyticsEvent> visitorEvents = entry.getValue();
                    AnalyticsEvent latest = visitorEvents.stream()
                            .max(Comparator.comparing(AnalyticsEvent::getRecordedAt))
                            .orElseThrow();
                    Map<String, List<AnalyticsEvent>> sessionGroups = visitorEvents.stream()
                            .collect(Collectors.groupingBy(AnalyticsEvent::getSessionId));
                    List<AnalyticsSessionDTO> sessions = sessionGroups.entrySet().stream()
                            .map(session -> toSessionDetails(session.getKey(), session.getValue()))
                            .sorted(Comparator.comparing(AnalyticsSessionDTO::startedAt).reversed())
                            .toList();
                    return new AnalyticsVisitorDTO(
                            entry.getKey(),
                            latest.getDevice(),
                            latest.getBrowser(),
                            latest.getRecordedAt(),
                            sessions
                    );
                })
                .sorted(Comparator.comparing(AnalyticsVisitorDTO::lastSeen).reversed())
                .toList();
    }

    private AnalyticsSessionDTO toSessionDetails(String sessionId, List<AnalyticsEvent> events) {
        Instant startedAt = events.stream()
                .map(AnalyticsEvent::getRecordedAt)
                .min(Instant::compareTo)
                .orElseThrow();
        Instant lastSeen = events.stream()
                .map(AnalyticsEvent::getRecordedAt)
                .max(Instant::compareTo)
                .orElseThrow();
        Map<String, Long> pageViews = new LinkedHashMap<>();
        for (String page : PAGES) {
            pageViews.put(page, events.stream()
                    .filter(event -> "PAGE_VIEW".equals(event.getType()) && page.equals(event.getPage()))
                    .count());
        }

        Map<String, Long> songs = new HashMap<>();
        Map<String, Long> selections = new HashMap<>();
        Map<String, Long> greetings = new HashMap<>();
        Map<String, Long> timelines = new HashMap<>();
        Map<String, Long> otherEvents = new HashMap<>();
        for (AnalyticsEvent event : events) {
            if ("SONG_PLAY".equals(event.getType()) && event.getSong() != null) {
                songs.merge(event.getSong(), 1L, Long::sum);
            } else if ("PROFILE_SELECT".equals(event.getType()) && event.getProfileName() != null) {
                selections.merge(event.getProfileName(), 1L, Long::sum);
            } else if ("PAGE_VIEW".equals(event.getType()) && event.getProfileName() != null &&
                    "greeting".equals(event.getPage())) {
                greetings.merge(event.getProfileName(), 1L, Long::sum);
            } else if ("PAGE_VIEW".equals(event.getType()) && event.getProfileName() != null &&
                    "timeline".equals(event.getPage())) {
                timelines.merge(event.getProfileName(), 1L, Long::sum);
            } else if (!Set.of("SESSION_START", "PAGE_VIEW", "PAGE_LEAVE", "THEME_SET", "THEME_CHANGE",
                    "SONG_PLAY", "SONG_PAUSE", "SONG_END", "PROFILE_SELECT").contains(event.getType())) {
                otherEvents.merge(event.getType(), 1L, Long::sum);
            }
        }

        return new AnalyticsSessionDTO(
                sessionId,
                startedAt,
                lastSeen,
                Collections.unmodifiableMap(pageViews),
                toCountList(songs, 50),
                toCountList(selections, 50),
                toCountList(greetings, 50),
                toCountList(timelines, 50),
                toCountList(otherEvents, 50),
                events.stream()
                        .sorted(Comparator.comparing(AnalyticsEvent::getRecordedAt))
                        .map(event -> new AnalyticsActivityDTO(
                                event.getRecordedAt(),
                                event.getType(),
                                event.getPage(),
                                event.getTheme(),
                                event.getSong(),
                                event.getProfileName(),
                                event.getDurationSeconds()))
                        .toList()
        );
    }

    private void validate(AnalyticsEventDTO dto) {
        if (dto == null || !isUuid(dto.getVisitorId()) || !isUuid(dto.getSessionId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid visitor and session IDs are required");
        }
        if (!EVENT_TYPES.contains(dto.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported analytics event");
        }
        if (dto.getPage() != null && !PAGES.contains(dto.getPage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported page");
        }
        if (dto.getTheme() != null && !Set.of("light", "dark").contains(dto.getTheme())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported theme");
        }
        if (dto.getSong() != null && (dto.getSong().isBlank() || dto.getSong().length() > 120)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Song must be 1 to 120 characters");
        }
        if (dto.getProfileName() != null &&
                (dto.getProfileName().isBlank() || dto.getProfileName().length() > 80)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile name must be 1 to 80 characters");
        }
        if (dto.getProfileName() != null &&
                !("PROFILE_SELECT".equals(dto.getType()) && "user-selection".equals(dto.getPage())) &&
                !("PAGE_VIEW".equals(dto.getType()) &&
                        ("greeting".equals(dto.getPage()) || "timeline".equals(dto.getPage())))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile name is only allowed for selection and profile page views");
        }
        if (dto.getDurationSeconds() != null &&
                (dto.getDurationSeconds() < 0 || dto.getDurationSeconds() > 86_400)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page duration");
        }
        if ("PAGE_VIEW".equals(dto.getType()) && dto.getPage() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page is required for a page view");
        }
        if ("SONG_PLAY".equals(dto.getType()) && dto.getSong() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Song is required for song playback");
        }
    }

    private boolean isUuid(String value) {
        if (value == null || value.length() != 36) {
            return false;
        }
        try {
            return UUID.fromString(value).toString().equalsIgnoreCase(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String detectBrowser(String userAgent) {
        String ua = normalizeUserAgent(userAgent);
        if (ua.contains("edg/")) return "Edge";
        if (ua.contains("opr/") || ua.contains("opera")) return "Opera";
        if (ua.contains("firefox/")) return "Firefox";
        if (ua.contains("chrome/") || ua.contains("chromium/")) return "Chrome";
        if (ua.contains("safari/")) return "Safari";
        return "Other";
    }

    private String detectDevice(String userAgent) {
        String ua = normalizeUserAgent(userAgent);
        if (ua.contains("ipad") || ua.contains("tablet")) return "Tablet";
        if (ua.contains("mobi") || ua.contains("iphone") || ua.contains("android")) return "Mobile";
        return "Desktop";
    }

    private String normalizeUserAgent(String userAgent) {
        if (userAgent == null) return "";
        return userAgent.substring(0, Math.min(userAgent.length(), 512)).toLowerCase(Locale.ROOT);
    }

    private void addVisitorCount(Map<String, Set<String>> counts, String category, String visitorId) {
        if (category != null) {
            counts.computeIfAbsent(category, ignored -> new HashSet<>()).add(visitorId);
        }
    }

    private List<AnalyticsCountDTO> toVisitorCountList(Map<String, Set<String>> counts) {
        return counts.entrySet().stream()
                .map(entry -> new AnalyticsCountDTO(entry.getKey(), entry.getValue().size()))
                .sorted(Comparator.comparingLong(AnalyticsCountDTO::count).reversed())
                .toList();
    }

    private List<AnalyticsCountDTO> toCountList(Map<String, Long> counts, int limit) {
        return counts.entrySet().stream()
                .map(entry -> new AnalyticsCountDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(AnalyticsCountDTO::count).reversed())
                .limit(limit)
                .toList();
    }
}
