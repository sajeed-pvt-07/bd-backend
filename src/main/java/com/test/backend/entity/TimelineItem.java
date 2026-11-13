package com.test.backend.entity;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "timeline_items")
@Data
public class TimelineItem {

    @Id
    private String id;

    private String userId;

    private String title;

    private String description;

    private String takeaway;

    private List<MediaItem> media;

    private List<ChatItem> chats;

    private LocalDate eventDate;

    private boolean special;

    private Boolean active = true;
}
