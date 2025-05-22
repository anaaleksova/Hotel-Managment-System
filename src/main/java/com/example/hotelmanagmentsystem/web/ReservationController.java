package com.example.hotelmanagmentsystem.web;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.model.*;
import com.example.hotelmanagmentsystem.service.impl.GuestServiceImpl;
import com.example.hotelmanagmentsystem.service.impl.ReservationService;
import com.example.hotelmanagmentsystem.service.impl.RoomService;
import com.example.hotelmanagmentsystem.service.impl.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private GuestServiceImpl guestService;
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
            AvailableRoomDto room = roomService.getRoomDetails(roomId,checkInDate);

            if (room == null) {
                return "redirect:/room-search?error=Room not found";
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User loggedUser = userDetails.getUser();
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

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
            Room room = roomService.getRoom(roomId);
            if (room == null) {
                throw new RuntimeException("Room not found");
            }

            Guest guest = guestService.findOrCreateGuest(firstName, lastName, email, phone, address);

            RoomPrice roomPrice = roomService.findRoomPrice(roomId, checkInDate);
            if (roomPrice == null) {
                throw new RuntimeException("Price not found for the selected room and dates");
            }

            Reservation reservation = new Reservation();
            reservation.setCheckIn(checkInDate);
            reservation.setCheckOut(checkOutDate);
            reservation.setStatus("Confirmed");
            reservation.setRoom(room);
            reservation.setGuest(guest);
            reservation.setRoomprice(roomPrice);

            Reservation savedReservation = reservationService.createReservation(reservation);

            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            guestService.addLoyaltyPoints(guest.getId(), (int)nights);

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
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/room-search";
        }
    }
    private boolean isStaff(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && "STAFF".equals(loggedUser.getUserType());
    }

}