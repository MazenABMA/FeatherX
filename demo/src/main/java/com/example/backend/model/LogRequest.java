package com.example.backend.model;

import java.time.LocalDateTime;

public class LogRequest {
    
    public static class Log {
        private String logMessage;
        private String logLevel;
        private LocalDateTime timestamp;

        // Constructor to initialize fields
        public Log(String logMessage, String logLevel, LocalDateTime timestamp) {
            this.logMessage = logMessage;
            this.logLevel = logLevel;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getLogMessage() {
            return logMessage;
        }

        public void setLogMessage(String logMessage) {
            this.logMessage = logMessage;
        }

        public String getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(String logLevel) {
            this.logLevel = logLevel;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}