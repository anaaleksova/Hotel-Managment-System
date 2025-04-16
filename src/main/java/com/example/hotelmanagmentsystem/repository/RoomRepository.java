package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Find all rooms that are available for the given date range
     * A room is considered available if:
     * 1. It doesn't have any overlapping "Not Available" entries in Room_Availability
     * 2. It doesn't have any confirmed reservations for the date range
     */
    @Query("""
        SELECT r FROM Room r
        WHERE r.id NOT IN (
            SELECT ra.id FROM RoomAvailability ra
            WHERE ra.status = 'booked'
            AND ra.date_from <= :dateTo
            AND ra.date_to >= :dateFrom
        )
        AND r.id NOT IN (
            SELECT res.id FROM Reservation res
            WHERE res.status = 'Confirmed'
            AND res.checkIn <= :dateTo
            AND res.checkOut >= :dateFrom
        )
    """)
    List<Room> findAvailableRoomsForDateRange(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );
}