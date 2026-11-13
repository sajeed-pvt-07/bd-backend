package com.test.backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String dp;
    private String background;
    private int precedence;
    private Boolean active = true;
}
