package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.dto.UserRegistrationDto;
import com.example.hotelmanagmentsystem.model.Group;
import com.example.hotelmanagmentsystem.model.Role;
import com.example.hotelmanagmentsystem.model.User;
import com.example.hotelmanagmentsystem.model.exceptions.EmailAlreadyExistsException;
import com.example.hotelmanagmentsystem.model.exceptions.UsernameAlreadyExistsException;
import com.example.hotelmanagmentsystem.repository.GroupRepository;
import com.example.hotelmanagmentsystem.repository.RoleRepository;
import com.example.hotelmanagmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

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
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setId((int) (Math.random()*10));
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setActive(true);
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setUserType("G"); // All new registrations are guests

        // Assign GUEST role
        user = userRepository.save(user);

        // Assign GUEST role
        Role guestRole = roleRepository.findByName("GUEST");
        user.addRole(guestRole);

        // Assign to CUSTOMERS group
        Group customersGroup = groupRepository.findByName("CUSTOMERS");
        user.addGroup(customersGroup);

        return userRepository.save(user);

    }

    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }
}
