package com.example.backend.model;

import java.time.LocalDateTime;

public class Log {
    private String eventName;
    private String logSource;
    private int eventCount;
    private LocalDateTime time;
    private String category;
    private String sourceIP;
    private String destIP;

    // Constructor
    public Log(String eventName, String logSource, int eventCount, LocalDateTime time, String category, String sourceIP, String destIP) {
        this.eventName = eventName;
        this.logSource = logSource;
        this.eventCount = eventCount;
        this.time = time;
        this.category = category;
        this.sourceIP = sourceIP;
        this.destIP = destIP;
    }

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLogSource() {
        return logSource;
    }

    public void setLogSource(String logSource) {
        this.logSource = logSource;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getDestIP() {
        return destIP;
    }

    public void setDestIP(String destIP) {
        this.destIP = destIP;
    }
}
