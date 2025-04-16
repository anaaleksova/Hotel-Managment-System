package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.LoyaltyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyLevelRepository extends JpaRepository<LoyaltyLevel, Integer> {

    /**
     * Find the appropriate loyalty level based on points
     * This assumes that loyalty levels have a minimum_points field
     */
    @Query("SELECT l FROM LoyaltyLevel l WHERE l.pointsRequired <= :points ORDER BY l.pointsRequired DESC LIMIT 1")
    Optional<LoyaltyLevel> findAppropriateLevel(@Param("points") Integer points);
}
