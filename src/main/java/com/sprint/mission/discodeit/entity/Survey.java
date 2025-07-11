package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Survey {
    private final UUID id;
    private long createdAt;
    private long updatedAt;
    private String question;
    private String[] answers;
    private long duration;
    private boolean isDuplicateResponseAllowed;

    public Survey(String question, String[] answers, long duration, boolean isDuplicateResponseAllowed) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.question = question;
        this.answers = answers;
        this.duration = duration;
        this.isDuplicateResponseAllowed = isDuplicateResponseAllowed;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isDuplicateResponseAllowed() {
        return isDuplicateResponseAllowed;
    }

    public void setDuplicateResponseAllowed(boolean duplicateResponseAllowed) {
        isDuplicateResponseAllowed = duplicateResponseAllowed;
    }
}
