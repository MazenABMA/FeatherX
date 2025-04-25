package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generated ID
    private Long id;  // ID for the log entity

    @Column(name = "log_message")  // Optional, for custom column names
    private String logMessage;

    @Column(name = "log_level")  // Optional, for custom column names
    private String logLevel;

    @Column(name = "timestamp")  // Optional, for custom column names
    private LocalDateTime timestamp;

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for logMessage
    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    // Getter and Setter for logLevel (this was missing)
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    // Getter and Setter for timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}