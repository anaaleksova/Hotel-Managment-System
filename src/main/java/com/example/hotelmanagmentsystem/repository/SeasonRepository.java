package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("SELECT s FROM Season s WHERE :date BETWEEN s.startDate AND s.endDate")
    Season findByDate(@Param("date") LocalDate date);
}
