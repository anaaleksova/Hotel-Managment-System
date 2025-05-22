package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.model.Room;
import com.example.hotelmanagmentsystem.model.RoomPositionType;
import com.example.hotelmanagmentsystem.model.RoomPrice;
import com.example.hotelmanagmentsystem.model.RoomType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomService {

    List<AvailableRoomDto> findAvailableRooms(LocalDate dateFrom, LocalDate dateTo);
    Room getRoom(Long roomId);
    AvailableRoomDto getRoomDetails(Long roomId, LocalDate checkInDate);
    RoomPrice findRoomPrice(Long roomId, LocalDate date);
    Optional<RoomPrice> findRoomPriceForDates(Long roomId, LocalDate date);
    List<Room> getAllRooms();
    void deleteRoom(Long id);
    Room getRoomById(Long id);
    List<RoomType> getAllRoomTypes();
    List<RoomPositionType> getAllRoomPositions();
}
