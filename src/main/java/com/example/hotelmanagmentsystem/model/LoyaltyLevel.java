package com.example.hotelmanagmentsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "loyalty_level")
public class LoyaltyLevel {
    @Id
    @Column(name = "loyalty_level_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "points_required")
    private Integer pointsRequired;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;


}