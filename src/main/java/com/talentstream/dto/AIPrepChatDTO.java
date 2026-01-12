package com.talentstream.dto;

import java.time.LocalDateTime;

public class AIPrepChatDTO {

    private Long chatId;
    private Long applicantId;
    private String title;
    private String savedChat;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AIPrepChatDTO() {
    }

    public AIPrepChatDTO(Long chatId, Long applicantId, String title, String savedChat, 
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.chatId = chatId;
        this.applicantId = applicantId;
        this.title = title;
        this.savedChat = savedChat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSavedChat() {
        return savedChat;
    }

    public void setSavedChat(String savedChat) {
        this.savedChat = savedChat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
