package com.test.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.test.backend.entity.ChatItem;
import com.test.backend.entity.MediaItem;
import lombok.Data;

@Data
public class TimelineItemDTO {

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
