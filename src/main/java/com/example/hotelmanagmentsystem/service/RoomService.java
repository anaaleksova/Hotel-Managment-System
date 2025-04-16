package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.model.*;
import com.example.hotelmanagmentsystem.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

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
        // First, find all rooms that are available for the date range
        List<Room> availableRooms = roomRepository.findAvailableRoomsForDateRange(dateFrom, dateTo);
        List<AvailableRoomDto> result = new ArrayList<>();

        // Find the season for the check-in date (we'll use this for pricing)
        Season season = seasonRepository.findByDate(dateFrom);

        if (season == null) {
            throw new RuntimeException("No season found for the given date range");
        }

        // For each available room, find its price and other details
        for (Room room : availableRooms) {
            // Get room type and position
            Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(room.getRoomTypeId());
            Optional<RoomPositionType> positionTypeOpt = roomPositionTypeRepository.findById(room.getPositionTypeId());

            if (!roomTypeOpt.isPresent() || !positionTypeOpt.isPresent()) {
                continue; // Skip rooms without type or position information
            }

            RoomType roomType = roomTypeOpt.get();
            RoomPositionType positionType = positionTypeOpt.get();

            // Find the price list for this room type, position, and season
            PriceList priceList = priceListRepository.findByRoomTypeAndPositionTypeAndSeason(
                    roomType.getId(),
                    positionType.getId(),
                    season.getId()
            );

            if (priceList == null) {
                continue; // Skip rooms without pricing information
            }

            // Create an AvailableRoomDto with all the information
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

    public AvailableRoomDto getRoomDetails(Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            return null;
        }

        Room room = roomOpt.get();
        RoomType roomType = roomTypeRepository.findById(room.getRoomTypeId()).orElse(null);
        RoomPositionType positionType = roomPositionTypeRepository.findById(room.getPositionTypeId()).orElse(null);

        if (roomType == null || positionType == null) {
            return null;
        }

        // Find pricing for the current season
        Optional<RoomPrice> roomPriceOpt = findRoomPriceForDates(roomId, LocalDate.now());
        if (!roomPriceOpt.isPresent()) {
            return null;
        }

        RoomPrice roomPrice = roomPriceOpt.get();
        PriceList priceList = roomPrice.getPriceList();

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
    private Optional<RoomPrice> findRoomPriceForDates(Long roomId, LocalDate date) {
        // Find the season for the date
        Season season = seasonRepository.findByDate(date);
        if (season == null) {
            return Optional.empty();
        }

        // Get the room
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            return Optional.empty();
        }

        Room room = roomOpt.get();
        RoomType roomType = roomTypeRepository.findById(room.getRoomTypeId()).orElse(null);
        RoomPositionType positionType =  roomPositionTypeRepository.findById(room.getPositionTypeId()).orElse(null);

        if (roomType == null || positionType == null) {
            return Optional.empty();
        }

        // Find a matching price list
        PriceList priceList = priceListRepository.findByRoomTypeAndPositionTypeAndSeason(
                roomType.getId(),
                positionType.getId(),
                season.getId()
        );

        if (priceList == null) {
            return Optional.empty();
        }

        // Find the RoomPrice entry
        return roomPriceRepository.findByRoomIdAndPriceListId(roomId, priceList.getId());
    }
}