package com.example.hotelmanagmentsystem.web;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.dto.RoomSearchDto;
import com.example.hotelmanagmentsystem.model.CustomUserDetails;
import com.example.hotelmanagmentsystem.model.Room;
import com.example.hotelmanagmentsystem.model.User;
import com.example.hotelmanagmentsystem.repository.RoomPositionTypeRepository;
import com.example.hotelmanagmentsystem.repository.RoomTypeRepository;
import com.example.hotelmanagmentsystem.repository.UserRepository;
import com.example.hotelmanagmentsystem.service.impl.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class RoomController {

    private RoomService roomService;
    private UserRepository userRepository;


    public RoomController(RoomService roomService, UserRepository userRepository, RoomPositionTypeRepository roomPositionTypeRepository, RoomTypeRepository roomTypeRepository) {
        this.roomService = roomService;
        this.userRepository = userRepository;
    }

    /**
     * Display the room search page
     */
    @GetMapping({"/room-search","/"})
    public String showRoomSearchPage(@AuthenticationPrincipal CustomUserDetails userDetails,Model model,HttpSession session) {
        if (isStaff()) {
            return "redirect:/room-management";
        }
        model.addAttribute("loggedUser", userDetails.getUser());
        RoomSearchDto searchDto = new RoomSearchDto();
        searchDto.setDateFrom(LocalDate.now());
        searchDto.setDateTo(LocalDate.now().plusDays(1));

        model.addAttribute("searchDto", searchDto);
        model.addAttribute("availableRooms", Collections.emptyList());
        model.addAttribute("searched", false);

        return "room-search";
    }

    /**
     * Process the room search form and show available rooms
     */
    @PostMapping("/room-search")
    public String searchRooms(@AuthenticationPrincipal CustomUserDetails userDetails,@ModelAttribute RoomSearchDto searchDto, Model model) {
        LocalDate dateFrom = searchDto.getDateFrom();
        LocalDate dateTo = searchDto.getDateTo();
        model.addAttribute("searched", true);

        model.addAttribute("loggedUser", userDetails.getUser());
        if (dateFrom == null || dateTo == null || dateFrom.isAfter(dateTo)) {
            model.addAttribute("error", "Please select valid date range (check-in date must be before check-out date)");
            model.addAttribute("availableRooms", Collections.emptyList());
            model.addAttribute("searched", true);
            return "room-search";
        }

        try {
            List<AvailableRoomDto> availableRooms = roomService.findAvailableRooms(dateFrom, dateTo);

            model.addAttribute("searchDto", searchDto);
            model.addAttribute("availableRooms", availableRooms);
            model.addAttribute("searched", true);

            return "room-search";
        } catch (Exception e) {
            model.addAttribute("error", "Error searching for available rooms: " + e.getMessage());
            model.addAttribute("availableRooms", Collections.emptyList());
            model.addAttribute("searched", true);
            return "room-search";
        }

    }

    private boolean isStaff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User loggedUser = userDetails.getUser();
        return loggedUser != null && "STAFF".equals(loggedUser.getUserType());
    }

    @GetMapping("/room-management")
    public String roomManagement(Model model, HttpSession session) {
        if (!isStaff()) {
            return "redirect:/";
        }

        List<Room> allRooms = roomService.getAllRooms();
        model.addAttribute("allRooms", allRooms);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User loggedUser = userDetails.getUser();
        model.addAttribute("loggedUser", loggedUser);

        return "room-management";
    }
}