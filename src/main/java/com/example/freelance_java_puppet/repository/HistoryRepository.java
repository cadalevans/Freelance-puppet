package com.example.freelance_java_puppet.repository;

import com.example.freelance_java_puppet.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History,Integer> {
}
