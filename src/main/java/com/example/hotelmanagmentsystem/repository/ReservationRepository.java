package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaSpecificationRepository<Reservation, Long> {
}


