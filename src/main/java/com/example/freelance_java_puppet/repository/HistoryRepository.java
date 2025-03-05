package com.example.freelance_java_puppet.repository;

import com.example.freelance_java_puppet.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History,Integer> {

    @Query("SELECT h FROM History h WHERE h NOT IN (SELECT u.histories FROM User u WHERE u.id = :userId)")
    List<History> findHistoriesNotPurchasedByUser(@Param("userId") int userId);

}
