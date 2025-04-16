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
}