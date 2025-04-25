package com.example.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.entity.LogEntity;
import org.springframework.stereotype.Repository;

@Repository

public interface LogRepository extends JpaRepository<LogEntity, Long> {
    // Additional query methods if needed
}