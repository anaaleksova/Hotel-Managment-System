package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    /**
     * Find availability entries that overlap with a given date range for a specific room
     */
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.id = :roomId AND " +
            "((ra.date_from <= :checkOutDate) AND (ra.date_to >= :checkInDate))")
    List<RoomAvailability> findOverlappingAvailabilities(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * Find availability entries for a specific room
     */
    List<RoomAvailability> findByRoomId(Long roomId);

    @Query("SELECT MAX(r.id) FROM RoomAvailability r")
    Optional<Integer> findMaxId();
}