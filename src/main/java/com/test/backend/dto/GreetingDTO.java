package com.test.backend.dto;

import lombok.Data;

@Data
public class GreetingDTO {

    private String id;
    private String userId;
    private String message;
    private Boolean active = true;
}
