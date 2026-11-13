package com.test.backend.repository;

import java.util.List;

import com.test.backend.entity.TimelineItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineItemRepository extends MongoRepository<TimelineItem, String> {

    List<TimelineItem> findByUserIdOrderByEventDateDesc(String userId);

    List<TimelineItem> findByUserIdAndActiveTrueOrderByEventDateDesc(String userId);
}
