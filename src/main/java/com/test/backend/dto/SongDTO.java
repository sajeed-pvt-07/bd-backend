package com.test.backend.dto;

import lombok.Data;
import com.test.backend.entity.SongType;

@Data
public class SongDTO {
    private String id;
    private String name;
    private String r2Url;
    private SongType type;
    private Integer priority;
    private Boolean active = true;
}
