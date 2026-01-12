package com.talentstream.dto;

import java.time.LocalDateTime;

public class ChatTitleDTO {

    private Long chatId;
    private String title;
    private LocalDateTime createdAt;

    public ChatTitleDTO() {
    }

    public ChatTitleDTO(Long chatId, String title, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.title = title;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
