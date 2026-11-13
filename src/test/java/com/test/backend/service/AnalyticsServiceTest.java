package com.test.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.argThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.test.backend.dto.AnalyticsEventDTO;
import com.test.backend.entity.AnalyticsEvent;
import com.test.backend.repository.AnalyticsEventRepository;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsEventRepository repository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void recordsValidatedEventsWithCoarseClientCategories() {
        AnalyticsEventDTO dto = event("PAGE_VIEW", "home");

        analyticsService.record(dto, "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) AppleWebKit/605.1.15 Version/17.0 Mobile Safari/604.1");

        verify(repository).save(any(AnalyticsEvent.class));
    }

    @Test
    void rejectsUnsupportedPagesBeforeSaving() {
        AnalyticsEventDTO dto = event("PAGE_VIEW", "greeting/name");

        assertThrows(ResponseStatusException.class, () -> analyticsService.record(dto, null));
    }

    @Test
    void acceptsOpenWhenPageViewsAndNoteOpenEvents() {
        analyticsService.record(event("PAGE_VIEW", "open-when"), null);
        analyticsService.record(event("OPEN_WHEN_NOTE_OPEN", "open-when"), null);
        analyticsService.record(event("JOURNEY_REPLAY", "thank-you"), null);

        verify(repository, org.mockito.Mockito.times(3)).save(any(AnalyticsEvent.class));
    }

    @Test
    void recordsSelectedProfileNameWithoutAcceptingNamesOnOtherEvents() {
        AnalyticsEventDTO selection = event("PROFILE_SELECT", "user-selection");
        selection.setProfileName("Ava");

        analyticsService.record(selection, null);

        verify(repository).save(argThat(saved -> "Ava".equals(saved.getProfileName())));

        AnalyticsEventDTO song = event("SONG_PLAY", "home");
        song.setSong("Song A");
        song.setProfileName("Ava");
        assertThrows(ResponseStatusException.class, () -> analyticsService.record(song, null));
    }

    @Test
    void summarizesVisitorsPagesAndDwellTime() {
        AnalyticsEvent firstView = savedEvent("PAGE_VIEW", "home", null, null);
        AnalyticsEvent leave = savedEvent("PAGE_LEAVE", "home", 30, null);
        AnalyticsEvent secondView = savedEvent("PAGE_VIEW", "user-selection", null, null);
        AnalyticsEvent greetingView = savedEvent("PAGE_VIEW", "greeting", null, null);
        greetingView.setProfileName("Ava");
        AnalyticsEvent selectedProfile = savedEvent("PROFILE_SELECT", "user-selection", null, null);
        selectedProfile.setProfileName("Ava");
        AnalyticsEvent songPlay = savedEvent("SONG_PLAY", "user-selection", null, "Song A");
        AnalyticsEvent openWhenView = savedEvent("PAGE_VIEW", "open-when", null, null);
        AnalyticsEvent noteOpened = savedEvent("OPEN_WHEN_NOTE_OPEN", "open-when", null, null);
        AnalyticsEvent replayed = savedEvent("JOURNEY_REPLAY", "thank-you", null, null);
        when(repository.findByRecordedAtAfter(any(Instant.class)))
                .thenReturn(List.of(
                        firstView, leave, secondView, greetingView, selectedProfile, songPlay,
                        openWhenView, noteOpened, replayed));

        var summary = analyticsService.getSummary(30);

        assertEquals(1, summary.uniqueVisitors());
        assertEquals(1, summary.sessions());
        assertEquals(4, summary.pageViews());
        assertEquals(30, summary.averagePageDurationSeconds());
        assertTrue(summary.topPages().stream().anyMatch(page -> page.name().equals("home")));
        var session = summary.visitorDetails().get(0).sessions().get(0);
        assertEquals(1, session.pageViews().get("greeting"));
        assertEquals("Ava", session.greetings().get(0).name());
        assertEquals(1, session.greetings().get(0).count());
        assertEquals("Ava", session.selections().get(0).name());
        assertEquals("Song A", session.songs().get(0).name());
        assertEquals(1, session.pageViews().get("open-when"));
        assertTrue(session.otherEvents().stream()
                .anyMatch(event -> event.name().equals("OPEN_WHEN_NOTE_OPEN") && event.count() == 1));
        assertTrue(session.otherEvents().stream()
                .anyMatch(event -> event.name().equals("JOURNEY_REPLAY") && event.count() == 1));
        assertEquals(9, session.activity().size());
    }

    @Test
    void supportsAllTimeSummaries() {
        when(repository.findAll()).thenReturn(List.of(savedEvent("PAGE_VIEW", "home", null, null)));

        var summary = analyticsService.getSummary(0);

        assertEquals(0, summary.days());
        assertEquals(1, summary.pageViews());
        verify(repository).findAll();
    }

    private AnalyticsEventDTO event(String type, String page) {
        AnalyticsEventDTO dto = new AnalyticsEventDTO();
        dto.setVisitorId(UUID.randomUUID().toString());
        dto.setSessionId(UUID.randomUUID().toString());
        dto.setType(type);
        dto.setPage(page);
        dto.setTheme("light");
        return dto;
    }

    private AnalyticsEvent savedEvent(String type, String page, Integer duration, String song) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setVisitorId("8f2f47d1-6f73-43bf-96c8-1a7848249bd4");
        event.setSessionId("4a5b3048-0808-47c6-9e4f-11564c879da9");
        event.setType(type);
        event.setPage(page);
        event.setTheme("light");
        event.setBrowser("Other");
        event.setDevice("Desktop");
        event.setDurationSeconds(duration);
        event.setSong(song);
        event.setRecordedAt(Instant.now());
        return event;
    }
}
