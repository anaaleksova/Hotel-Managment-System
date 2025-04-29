package com.example.hotelmanagmentsystem.web;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.model.*;
import com.example.hotelmanagmentsystem.service.GuestService;
import com.example.hotelmanagmentsystem.service.ReservationService;
import com.example.hotelmanagmentsystem.service.RoomService;
import com.example.hotelmanagmentsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private GuestService guestService;
    @Autowired
    private UserService userService;

    /**
     * Show the reservation form with room details
     */
    @GetMapping("/create")
    public String showReservationForm(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            Model model,
            HttpSession session) {

        try {
            // Get room details
            AvailableRoomDto room = roomService.getRoomDetails(roomId,checkInDate);

            if (room == null) {
                return "redirect:/room-search?error=Room not found";
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User loggedUser = userDetails.getUser();
            // Calculate number of nights
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            // Add attributes to model
            model.addAttribute("room", room);
            model.addAttribute("checkInDate", checkInDate);
            model.addAttribute("checkOutDate", checkOutDate);
            model.addAttribute("nights", nights);
            model.addAttribute("loggedUser",loggedUser);

            return "reservation-form";
        } catch (Exception e) {
            return "redirect:/room-search?error=" + e.getMessage();
        }
    }

    /**
     * Process the reservation form and create a booking
     */
    @PostMapping("/confirm")
    public String confirmReservation(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String address,
            @RequestParam int numberOfGuests,
            @RequestParam(required = false) String specialRequests,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // Get the room
            Room room = roomService.getRoom(roomId);
            if (room == null) {
                throw new RuntimeException("Room not found");
            }

            // Create or retrieve guest
            Guest guest = guestService.findOrCreateGuest(firstName, lastName, email, phone, address);

            // Find the appropriate RoomPrice
            RoomPrice roomPrice = roomService.findRoomPrice(roomId, checkInDate);
            if (roomPrice == null) {
                throw new RuntimeException("Price not found for the selected room and dates");
            }

            // Create the reservation
            Reservation reservation = new Reservation();
            reservation.setCheckIn(checkInDate);
            reservation.setCheckOut(checkOutDate);
            reservation.setStatus("Confirmed");
            reservation.setRoom(room);
            reservation.setGuest(guest);
            reservation.setRoomprice(roomPrice);

            // Save the reservation and mark the room as booked
            Reservation savedReservation = reservationService.createReservation(reservation);

            // Calculate the number of nights for loyalty points (if needed)
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            // Add loyalty points based on stay (1 point per night)
            guestService.addLoyaltyPoints(guest.getId(), (int)nights);

            // Add to model for confirmation page
            model.addAttribute("reservation", savedReservation);
            model.addAttribute("roomNumber", room.getRoomNumber());
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            model.addAttribute("numberOfGuests", numberOfGuests);
            model.addAttribute("specialRequests", specialRequests);
            model.addAttribute("loyaltyPoints", guest.getLoyaltyPoints());
            model.addAttribute("loyaltyLevel", guest.getLoyaltyLevel().getName());

            return "booking-confirmation";
        } catch (Exception e) {
            // If there's an error, redirect back to room search with error message
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/room-search";
        }
    }
    private boolean isStaff(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && "STAFF".equals(loggedUser.getUserType());
    }
//    @GetMapping("/")
//    public String roomManagement(Model model, HttpSession session) {
//        // Check if user is staff
//        if (!isStaff(session)) {
//            return "redirect:/";
//        }
//
//        // Get all rooms for staff view (unfiltered)
//        List<Room> allRooms = roomService.getAllRooms();
//        model.addAttribute("allRooms", allRooms);
//
//        // Add the logged user to the model
//        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
//
//        return "room-search";
//    }
    // View reservation details
//    @GetMapping("/reservations/view/{id}")
//    public String viewReservation(@PathVariable Long id, Model model, HttpSession session) {
//        // Staff can view any reservation, guests can only view their own
//        User loggedUser = (User) session.getAttribute("loggedUser");
//        if (loggedUser == null) {
//            return "redirect:/login";
//        }
//
//        Reservation reservation = reservationService.getReservationById(id);
//
//        // If not staff, check if reservation belongs to user
//        if (!"STAFF".equals(loggedUser.getUserType()) &&
//                !reservation.getGuest().getId().equals(loggedUser.getId())) {
//            return "redirect:/";
//        }
//
//        model.addAttribute("reservation", reservation);
//        model.addAttribute("loggedUser", loggedUser);
//
//        return "reservation-detail";
//    }
}