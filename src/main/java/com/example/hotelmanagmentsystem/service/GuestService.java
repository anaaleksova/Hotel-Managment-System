package com.example.hotelmanagmentsystem.service;

import com.example.hotelmanagmentsystem.model.Guest;
import com.example.hotelmanagmentsystem.model.LoyaltyLevel;
import com.example.hotelmanagmentsystem.repository.GuestRepository;
import com.example.hotelmanagmentsystem.repository.LoyaltyLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private LoyaltyLevelRepository loyaltyLevelRepository;

    /**
     * Find a guest by email or create a new one if not found
     */
    @Transactional
    public Guest findOrCreateGuest(String firstName, String lastName, String email, String phone, String address) {
        // Try to find an existing guest with this email
        Optional<Guest> existingGuest = guestRepository.findByEmail(email);

        if (existingGuest.isPresent()) {
            // Update the guest details if needed
            Guest guest = existingGuest.get();
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setPhone(phone);
            if (address != null && !address.isEmpty()) {
                guest.setAddress(address);
            }
            return guestRepository.save(guest);
        } else {
            // Create a new guest
            Guest newGuest = new Guest();
            newGuest.setFirstName(firstName);
            newGuest.setLastName(lastName);
            newGuest.setEmail(email);
            newGuest.setPhone(phone);
            newGuest.setAddress(address);

            // Set initial loyalty points
            newGuest.setLoyaltyPoints(0);

            // Set initial loyalty level (assuming there's a basic/initial level with ID 1)
            LoyaltyLevel basicLevel = loyaltyLevelRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Basic loyalty level not found"));
            newGuest.setLoyaltyLevel(basicLevel);

            // No user association initially
            newGuest.setUser(null);

            // Generate next ID (if you're managing IDs manually)
            Integer nextId = guestRepository.findMaxId().orElse(0) + 1;
            newGuest.setId(nextId);

            return guestRepository.save(newGuest);
        }
    }

    /**
     * Get a guest by ID
     */
    public Guest getGuest(Integer id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
    }

    /**
     * Add loyalty points to a guest
     */
    @Transactional
    public Guest addLoyaltyPoints(Integer guestId, Integer points) {
        Guest guest = getGuest(guestId);
        Integer currentPoints = guest.getLoyaltyPoints() != null ? guest.getLoyaltyPoints() : 0;
        guest.setLoyaltyPoints(currentPoints + points);

        // Check if guest should be upgraded to a new loyalty level
        updateLoyaltyLevelIfNeeded(guest);

        return guestRepository.save(guest);
    }

    /**
     * Update guest's loyalty level based on their points
     */
    private void updateLoyaltyLevelIfNeeded(Guest guest) {
        Integer points = guest.getLoyaltyPoints();
        if (points == null) {
            return;
        }

        // Find the appropriate loyalty level based on points
        LoyaltyLevel appropriateLevel = loyaltyLevelRepository.findAppropriateLevel(points)
                .orElse(guest.getLoyaltyLevel()); // Keep current level if no appropriate level found

        guest.setLoyaltyLevel(appropriateLevel);
    }
}
