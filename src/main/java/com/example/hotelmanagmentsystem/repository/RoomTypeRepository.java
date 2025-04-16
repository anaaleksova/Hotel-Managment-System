package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {}
