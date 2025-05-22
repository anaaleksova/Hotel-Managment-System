package com.example.hotelmanagmentsystem.service.impl;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.model.*;
import com.example.hotelmanagmentsystem.repository.*;
import com.example.hotelmanagmentsystem.service.IRoomService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private RoomPriceRepository roomPriceRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomPositionTypeRepository roomPositionTypeRepository;

    /**
     * Find available rooms for the given date range and return them with pricing information
     */
    public List<AvailableRoomDto> findAvailableRooms(LocalDate dateFrom, LocalDate dateTo) {
        List<Room> availableRooms = roomRepository.findAvailableRoomsForDateRange(dateFrom, dateTo);
        List<AvailableRoomDto> result = new ArrayList<>();

        Season season = seasonRepository.findByDate(dateFrom);

        if (season == null) {
            throw new RuntimeException("No season found for the given date range");
        }

        for (Room room : availableRooms) {
            Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(room.getRoomType().getId());
            Optional<RoomPositionType> positionTypeOpt = roomPositionTypeRepository.findById(room.getRoomPositionType().getId());

            if (!roomTypeOpt.isPresent() || !positionTypeOpt.isPresent()) {
                continue;
            }

            RoomType roomType = roomTypeOpt.get();
            RoomPositionType positionType = positionTypeOpt.get();

            PriceList priceList = priceListRepository.findByRoomTypeAndPositionTypeAndSeason(
                    roomType.getId(),
                    positionType.getId(),
                    season.getId()
            );

            if (priceList == null) {
                continue;
            }

            AvailableRoomDto dto = new AvailableRoomDto();
            dto.setRoomId(room.getId());
            dto.setRoomNumber(room.getRoomNumber());
            dto.setRoomTypeName(roomType.getName());
            dto.setRoomPositionName(positionType.getName());
            dto.setPrice(priceList.getPrice());
            dto.setAmenities(room.getAmenities());
            dto.setCapacity(room.getCapacity());
            dto.setFloor(room.getFloor());

            result.add(dto);
        }

        return result;
    }

    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow();
    }

    public AvailableRoomDto getRoomDetails(Long roomId, LocalDate checkInDate) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return null;
        }

        Room room = roomOpt.get();

        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(room.getRoomType().getId());
        Optional<RoomPositionType> positionTypeOpt = roomPositionTypeRepository.findById(room.getRoomPositionType().getId());

        if (roomTypeOpt.isEmpty() || positionTypeOpt.isEmpty()) {
            return null;
        }

        RoomType roomType = roomTypeOpt.get();
        RoomPositionType positionType = positionTypeOpt.get();

        Season season = seasonRepository.findByDate(checkInDate);
        if (season == null) {
            return null;
        }

        PriceList priceList = priceListRepository.findByRoomTypeAndPositionTypeAndSeason(
                roomType.getId(),
                positionType.getId(),
                season.getId()
        );

        if (priceList == null) {
            return null;
        }

        AvailableRoomDto dto = new AvailableRoomDto();
        dto.setRoomId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomTypeName(roomType.getName());
        dto.setRoomPositionName(positionType.getName());
        dto.setPrice(priceList.getPrice());
        dto.setAmenities(room.getAmenities());
        dto.setCapacity(room.getCapacity());
        dto.setFloor(room.getFloor());

        return dto;
    }

    /**
     * Find a RoomPrice entry for a room and date
     */
    public RoomPrice findRoomPrice(Long roomId, LocalDate date) {
        Optional<RoomPrice> roomPriceOpt = findRoomPriceForDates(roomId, date);
        return roomPriceOpt.orElse(null);
    }

    /**
     * Helper method to find a RoomPrice for a specific room and date
     */
    public Optional<RoomPrice> findRoomPriceForDates(Long roomId, LocalDate date) {
        Season season = seasonRepository.findByDate(date);
        if (season == null) {
            return Optional.empty();
        }

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId()).orElse(null);
        RoomPositionType positionType =  roomPositionTypeRepository.findById(room.getRoomPositionType().getId()).orElse(null);

        if (roomType == null || positionType == null) {
            return Optional.empty();
        }

        PriceList priceList = priceListRepository.findByRoomTypeAndPositionTypeAndSeason(
                roomType.getId(),
                positionType.getId(),
                season.getId()
        );

        if (priceList == null) {
            return Optional.empty();
        }

        return roomPriceRepository.findByRoomIdAndPriceListId(roomId, priceList.getId());
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow();
    }

    public void deleteRoom(Long id) {
        roomRepository.delete(roomRepository.findById(id).orElseThrow());
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
    public List<RoomPositionType> getAllRoomPositions() {
        return roomPositionTypeRepository.findAll();
    }
}