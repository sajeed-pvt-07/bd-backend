package com.test.backend.entity;

import lombok.Data;

@Data
public class MediaItem {

    private MediaType type;

    private String url;

    private String caption;
}
