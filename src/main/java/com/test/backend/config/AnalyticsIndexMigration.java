package com.test.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.test.backend.entity.AnalyticsEvent;

@Component
public class AnalyticsIndexMigration implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsIndexMigration.class);
    private final MongoTemplate mongoTemplate;

    public AnalyticsIndexMigration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        mongoTemplate.indexOps(AnalyticsEvent.class).getIndexInfo().stream()
                .filter(index -> index.getExpireAfter().isPresent())
                .forEach(index -> {
                    mongoTemplate.indexOps(AnalyticsEvent.class).dropIndex(index.getName());
                    logger.info("Removed TTL index '{}' from analytics events to retain records indefinitely", index.getName());
                });
    }
}
