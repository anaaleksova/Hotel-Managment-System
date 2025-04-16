package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.model.Reservation;
import com.example.hotelmanagmentsystem.model.RoomAvailability;
import com.example.hotelmanagmentsystem.repository.ReservationRepository;
import com.example.hotelmanagmentsystem.repository.RoomAvailabilityRepository;
import com.example.hotelmanagmentsystem.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;
    @Autowired
    private RoomRepository roomRepository;

    /**
     * Create a new reservation and mark the room as booked in Room_Availability
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Check if the room is available for the selected dates
        Long roomId = reservation.getRoom().getId();
        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        // Check for overlapping reservations
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                roomId, checkIn, checkOut);

        if (!overlappingReservations.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Check for overlapping availability entries
        List<RoomAvailability> overlappingAvailabilities = roomAvailabilityRepository.findOverlappingAvailabilities(
                roomId, checkIn, checkOut);

        if (!overlappingAvailabilities.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        // Generate next ID (assuming you're managing IDs manually)
        Integer nextId = reservationRepository.findMaxId().orElse(0) + 1;
        reservation.setId(Long.valueOf(nextId));

        // Save the reservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Create entry in Room_Availability to mark the room as booked
        Integer nextavailabilityId = roomAvailabilityRepository.findMaxId().orElse(0) + 1;
        RoomAvailability availability = new RoomAvailability();
        availability.setId(nextavailabilityId);
        availability.setRoom(roomRepository.findById(roomId).orElseThrow());
        availability.setDate_from(checkIn);
        availability.setDate_to(checkOut);
        availability.setStatus("booked");

        roomAvailabilityRepository.save(availability);

        return savedReservation;
    }

    /**
     * Get a reservation by ID
     */
    public Reservation getReservation(Integer id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    /**
     * Cancel a reservation
     */
    @Transactional
    public void cancelReservation(Integer id) {
        Reservation reservation = getReservation(id);
        reservation.setStatus("Cancelled");
        reservationRepository.save(reservation);

        // Make the room available again by removing the entry from Room_Availability
        Long roomId = reservation.getRoom().getId();
        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        List<RoomAvailability> availabilities = roomAvailabilityRepository.findOverlappingAvailabilities(
                roomId, checkIn, checkOut);

        roomAvailabilityRepository.deleteAll(availabilities);
    }

    /**
     * Get reservations by guest ID
     */
    public List<Reservation> getReservationsByGuestId(Integer guestId) {
        return reservationRepository.findByGuestId(guestId);
    }
}