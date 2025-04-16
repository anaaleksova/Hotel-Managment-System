package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.RoomPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomPriceRepository extends JpaRepository<RoomPrice, Long> {
    List<RoomPrice> findByRoomId(Long roomId);

    Optional<RoomPrice> findByRoomIdAndPriceListId(Long roomId, Integer id);
}
