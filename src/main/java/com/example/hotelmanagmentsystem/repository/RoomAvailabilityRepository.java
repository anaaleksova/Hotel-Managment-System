package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

//    List<RoomAvailability> findByRoomIdAndDateFromLessThanEqualAndDateToGreaterThanEqual(
//            Long roomId, LocalDate dateTo, LocalDate dateFrom);
}
