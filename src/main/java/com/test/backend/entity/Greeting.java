package com.test.backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "greetings")
@Data
public class Greeting {

    @Id
    private String id;
    private String userId;
    private String message;
    private Boolean active = true;

}
