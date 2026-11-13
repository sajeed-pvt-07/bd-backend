package com.test.backend.entity;

import lombok.Data;

@Data
public class ChatItem {

    private ChatSpeaker speaker;

    private String message;
}
