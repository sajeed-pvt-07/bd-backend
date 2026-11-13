package com.test.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Config {

    @Id
    private String id;
    private String key;
    private String value;
}
