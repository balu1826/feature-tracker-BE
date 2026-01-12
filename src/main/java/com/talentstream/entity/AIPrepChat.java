package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Entity
@Table(name = "ai_prep_chats")
public class AIPrepChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "saved_chat", columnDefinition = "TEXT")
    private String savedChat;   // Stores full JSON chat history

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private Gson gson = new Gson();

    public AIPrepChat() {
        this.createdAt = LocalDateTime.now();
        this.savedChat = "[]"; // Initialize empty chat array
    }

    public AIPrepChat(Long applicantId, String title, String savedChat) {
        this.applicantId = applicantId;
        this.title = title;
        this.savedChat = savedChat == null ? "[]" : savedChat;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private List<ChatMessage> getChatHistory() {
        if (savedChat == null || savedChat.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return gson.fromJson(savedChat, new TypeToken<List<ChatMessage>>() {}.getType());
    }

    /** Save chat history list back into savedChat JSON */
    private void updateSavedChat(List<ChatMessage> history) {
        this.savedChat = gson.toJson(history);
    }

    /** Append new message to chat */
    public void addMessage(String role, String message) {
        List<ChatMessage> history = getChatHistory();
        history.add(new ChatMessage(role, message, LocalDateTime.now().toString()));
        updateSavedChat(history);
    }

    /** Get last question asked by user */
    public String getLastUserQuestion() {
        List<ChatMessage> history = getChatHistory();

        for (int i = history.size() - 1; i >= 0; i--) {
            ChatMessage msg = history.get(i);
            if (msg.getRole().equals("user")) {
                return msg.getMessage();
            }
        }
        return null;
    }

    private static class ChatMessage {
        private String role;
        private String message;
        private String timestamp;

        public ChatMessage(String role, String message, String timestamp) {
            this.role = role;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public String getMessage() { return message; }
    }


    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSavedChat() { return savedChat; }
    public void setSavedChat(String savedChat) { this.savedChat = savedChat; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
