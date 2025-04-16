package com.example.hotelmanagmentsystem.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class RoomSearchDto {
        private LocalDate dateFrom;
        private LocalDate dateTo;
}
