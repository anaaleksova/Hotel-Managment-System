package com.example.hotelmanagmentsystem.web;

import com.example.hotelmanagmentsystem.dto.AvailableRoomDto;
import com.example.hotelmanagmentsystem.dto.RoomSearchDto;
import com.example.hotelmanagmentsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RoomService roomService;

    /**
     * Display the room search page
     */
    @GetMapping({"/room-search","/"})
    public String showRoomSearchPage(Model model) {
        // Initialize search form with default values (today and tomorrow)
        RoomSearchDto searchDto = new RoomSearchDto();
        searchDto.setDateFrom(LocalDate.now());
        searchDto.setDateTo(LocalDate.now().plusDays(1));

        model.addAttribute("searchDto", searchDto);
        // Add empty list for initial page load
        model.addAttribute("availableRooms", Collections.emptyList());
        model.addAttribute("searched", false);

        return "room-search";
    }

    /**
     * Process the room search form and show available rooms
     */
    @PostMapping("/room-search")
    public String searchRooms(@ModelAttribute RoomSearchDto searchDto, Model model) {
        LocalDate dateFrom = searchDto.getDateFrom();
        LocalDate dateTo = searchDto.getDateTo();

        // Validate dates
        if (dateFrom == null || dateTo == null || dateFrom.isAfter(dateTo)) {
            model.addAttribute("error", "Please select valid date range (check-in date must be before check-out date)");
            model.addAttribute("availableRooms", Collections.emptyList());
            model.addAttribute("searched", true);
            return "room-search";
        }

        try {
            // Find available rooms for the selected date range
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
}