package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.model.Reservation;

import java.util.List;

public interface IReservationService {

    Reservation createReservation(Reservation reservation);
    Reservation getReservation(Integer id);
    void cancelReservation(Integer id);
    List<Reservation> getReservationsByGuestId(Integer guestId);
    Reservation getReservationById(Long id);

}
