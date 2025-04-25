package com.example.backend.service;
import com.example.backend.repository.LogRepository;
import com.example.backend.entity.LogEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import com.example.backend.model.LogRequest; // Import LogRequest if it exists in this package

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    // Method to save logs
    public void saveLogs(List<LogRequest.Log> logs) {
        for (LogRequest.Log log : logs) {
            // Convert LogRequest.Log to LogEntity
            LogEntity logEntity = new LogEntity();
            // logEntity.setLogMessage(log.getLogMessage());
            // logEntity.setLogLevel(log.getLogLevel());
            // logEntity.setTimestamp(log.getTimestamp());  // This line uses getTimestamp()

            // Save the log entity to the database
            logRepository.save(logEntity);
        }
    }
}