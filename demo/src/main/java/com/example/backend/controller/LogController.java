package com.example.backend.controller;

 // Removed as the Log class is not resolved
import com.example.backend.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.backend.model.LogRequest; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;



// import com.example.backend.service.LogService;
import com.example.backend.model.LogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {}

   
    // private LogService logService;  // Injecting LogService

    // Endpoint to receive logs and save them
 
//         try {
//             logService.saveLogs(logRequest.getLogs());  // Extract and pass the list of logs
//             return ResponseEntity.ok("Logs successfully saved!");
//         } catch (Exception e) {
//             return ResponseEntity.status(500).body("Failed to save logs: " + e.getMessage());
//         }
//     }
// }
