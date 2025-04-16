package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {

    /**
     * Find a guest by email
     */
    Optional<Guest> findByEmail(String email);

    /**
     * Find the maximum guest ID (for auto-incrementing manually)
     */
    @Query("SELECT MAX(g.id) FROM Guest g")
    Optional<Integer> findMaxId();
}
