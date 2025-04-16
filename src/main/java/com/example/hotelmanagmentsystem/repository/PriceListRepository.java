package com.example.hotelmanagmentsystem.repository;

import com.example.hotelmanagmentsystem.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    @Query("""
        SELECT pl FROM PriceList pl
        WHERE pl.roomTypeRoomType.id = :roomTypeId
        AND pl.roomPositionTypePositionType.id = :positionTypeId
        AND pl.seasonSeason.id = :seasonId
    """)
    PriceList findByRoomTypeAndPositionTypeAndSeason(
            @Param("roomTypeId") Long roomTypeId,
            @Param("positionTypeId") Long positionTypeId,
            @Param("seasonId") Long seasonId
    );
}
