package com.example.hotelmanagmentsystem.service.impl;

import com.example.hotelmanagmentsystem.model.*;
import com.example.hotelmanagmentsystem.repository.*;
import com.example.hotelmanagmentsystem.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private RoomPositionTypeRepository roomPositionTypeRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new reservation and mark the room as booked in Room_Availability
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        Long roomId = reservation.getRoom().getId();
        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                roomId, checkIn, checkOut);

        if (!overlappingReservations.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        List<RoomAvailability> overlappingAvailabilities = roomAvailabilityRepository.findOverlappingAvailabilities(
                roomId, checkIn, checkOut);

        if (!overlappingAvailabilities.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        Integer nextId = reservationRepository.findMaxId().orElse(0) + 1;
        reservation.setId(Long.valueOf(nextId));

        Reservation savedReservation = reservationRepository.save(reservation);

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

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id);
    }


}