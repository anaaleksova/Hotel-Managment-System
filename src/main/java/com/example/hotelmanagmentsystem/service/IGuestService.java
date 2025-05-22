package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.model.Guest;

public interface IGuestService {

    Guest findOrCreateGuest(String firstName, String lastName, String email, String phone, String address);
    Guest getGuest(Integer id);
    Guest addLoyaltyPoints(Integer guestId, Integer points);
    void updateLoyaltyLevelIfNeeded(Guest guest);
}
