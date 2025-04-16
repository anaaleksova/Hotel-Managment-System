package com.example.hotelmanagmentsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
@Table(name = "room")
public class Room {
    @Id
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "capacity")
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "is_smoking")
    private Boolean isSmoking;

    @Column(name = "amenities", length = Integer.MAX_VALUE)
    private String amenities;

    @Column(name = "reserved")
    private Boolean reserved;

    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Column(name = "position_type_id")
    private Long positionTypeId;

}