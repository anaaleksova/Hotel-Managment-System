package com.example.hotelmanagmentsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "room_availability")
public class RoomAvailability {
    @Id
    @Column(name = "availability_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "date_from")
    private LocalDate date_from;

    @Column(name = "date_to")
    private LocalDate date_to;

    @Column(name = "status", length = 20)
    private String status;

}