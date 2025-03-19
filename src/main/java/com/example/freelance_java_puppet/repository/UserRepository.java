package com.example.freelance_java_puppet.repository;

import com.example.freelance_java_puppet.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);

    User findByFirstName(String name);


    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.card = null WHERE u.id = :userId")
    void removeCardFromUser(@Param("userId") int userId);
}
