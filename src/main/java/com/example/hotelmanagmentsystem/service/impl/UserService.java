package com.example.hotelmanagmentsystem.service.impl;

import com.example.hotelmanagmentsystem.dto.UserRegistrationDto;
import com.example.hotelmanagmentsystem.model.Group;
import com.example.hotelmanagmentsystem.model.Role;
import com.example.hotelmanagmentsystem.model.User;
import com.example.hotelmanagmentsystem.model.exceptions.EmailAlreadyExistsException;
import com.example.hotelmanagmentsystem.model.exceptions.UsernameAlreadyExistsException;
import com.example.hotelmanagmentsystem.repository.GroupRepository;
import com.example.hotelmanagmentsystem.repository.RoleRepository;
import com.example.hotelmanagmentsystem.repository.UserRepository;
import com.example.hotelmanagmentsystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setId((int) (Math.random()*10));
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setActive(true);
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setUserType("G");

        user = userRepository.save(user);

        Role guestRole = roleRepository.findByName("GUEST");
        user.addRole(guestRole);

        Group customersGroup = groupRepository.findByName("CUSTOMERS");
        user.addGroup(customersGroup);

        return userRepository.save(user);

    }

    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }
}
