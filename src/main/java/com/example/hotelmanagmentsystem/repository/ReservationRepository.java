package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    /**
     * Find reservations that overlap with a given date range for a specific room
     */
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.status = 'Confirmed' AND " +
            "((r.checkIn <= :checkOut) AND (r.checkOut >= :checkIn))")
    List<Reservation> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    /**
     * Find reservations by guest ID
     */
    List<Reservation> findByGuestId(Integer guestId);

    /**
     * Find the maximum reservation ID (for auto-incrementing manually)
     */
    @Query("SELECT MAX(r.id) FROM Reservation r")
    Optional<Integer> findMaxId();
}

