package com.test.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "songs")
public class Song {

    @Id
    private String id;

    private String name;

    private String r2Url;

    private SongType type;

    private Integer priority;

    private Boolean active = true;
}
