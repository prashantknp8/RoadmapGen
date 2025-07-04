package com.roadmapgen.demo.repository;


import com.roadmapgen.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by email for login
    Optional<User> findByEmail(String email);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.course WHERE u.id = :id")
    Optional<User> findByIdWithCourses(@Param("id") Integer id);

    // Optional: Check if a user already exists with a given email
    boolean existsByEmail(String email);
}
