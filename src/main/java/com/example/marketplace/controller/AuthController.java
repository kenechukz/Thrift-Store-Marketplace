package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;                                                                                                                                                                   
import org.springframework.security.crypto.password.PasswordEncoder;  

import jakarta.servlet.http.HttpSession;

import com.example.marketplace.repoository.UserRepository;
import com.example.marketplace.model.User;
import com.example.marketplace.service.AuthService;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }


    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){

        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute User loginForm, Model model, HttpServletRequest request){
        String email = loginForm.getEmail();                                                                                                                                                                                             
        String rawPassword = loginForm.getPassword();

        Optional<User> optionalUser = this.authService.authenticate(email, rawPassword);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Invalid email or password. Try again!");
            return "login";
        }

        HttpSession session = request.getSession();
        
        User user = optionalUser.get();

        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request){

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User loginForm, @RequestParam("confirmPassword") String confirmedPassword, Model model, HttpServletRequest request){

        String email = loginForm.getEmail();
        String registeredPassword = loginForm.getPassword();

        if (userRepository.findByEmail((email)).isPresent()){
            model.addAttribute("error", "This email is taken. Please use a different email!");
            return "register";
        }

        if (!registeredPassword.equals(confirmedPassword)){
            model.addAttribute("error", "Passwords do not match, try again!");
            return "register";
        }

        User newUser = new User(loginForm.getFirstName(), loginForm.getLastName(), email, passwordEncoder.encode(registeredPassword));
        userRepository.save(newUser);
        return "redirect:/login";
    }
}
