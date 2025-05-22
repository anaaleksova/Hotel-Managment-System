package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.dto.UserRegistrationDto;
import com.example.hotelmanagmentsystem.model.User;

import java.util.Optional;

public interface IUserService {

    User registerNewUser(UserRegistrationDto registrationDto);
    Optional<User> findByUsername(String username);
}
