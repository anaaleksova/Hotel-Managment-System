package com.example.hotelmanagmentsystem.web;

import com.example.hotelmanagmentsystem.dto.UserRegistrationDto;
import com.example.hotelmanagmentsystem.model.User;
import com.example.hotelmanagmentsystem.model.exceptions.EmailAlreadyExistsException;
import com.example.hotelmanagmentsystem.model.exceptions.UsernameAlreadyExistsException;
import com.example.hotelmanagmentsystem.service.impl.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByUsername(username).orElseThrow();
            session.setAttribute("loggedUser", user);

            if ("STAFF".equals(user.getUserType())) {
                return "redirect:/room-management";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerNewUser(registrationDto);
            return "redirect:/login?registered";
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}
