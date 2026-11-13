package com.test.backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.test.backend.entity.AnalyticsEvent;

public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {
    List<AnalyticsEvent> findByRecordedAtAfter(Instant recordedAt);
}
