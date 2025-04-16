package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.RoomPositionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomPositionTypeRepository extends JpaRepository<RoomPositionType, Long> {}
