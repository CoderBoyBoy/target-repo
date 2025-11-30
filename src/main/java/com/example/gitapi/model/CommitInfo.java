package com.example.gitapi.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a Git commit.
 */
public class CommitInfo {

    private String id;
    private String message;
    private String author;
    private String authorEmail;
    private LocalDateTime timestamp;

    public CommitInfo() {
    }

    public CommitInfo(String id, String message, String author, String authorEmail, LocalDateTime timestamp) {
        this.id = id;
        this.message = message;
        this.author = author;
        this.authorEmail = authorEmail;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
