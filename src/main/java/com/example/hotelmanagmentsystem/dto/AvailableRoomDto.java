package com.example.hotelmanagmentsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AvailableRoomDto {
    private Long roomId;
    private String roomNumber;
    private String roomTypeName;
    private String roomPositionName;
    private BigDecimal price;
    private String amenities;
    private int capacity;
    private int floor;
}
