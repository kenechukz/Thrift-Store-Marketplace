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

import com.example.marketplace.repoository.UserRepository;
import com.example.marketplace.model.User;

import java.util.Optional;

@Controller
public class AuthController {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){

        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute User loginForm, Model model, HttpServletRequest request){
        String email = loginForm.getEmail();                                                                                                                                                                                             
        String rawPassword = loginForm.getPassword();

        Optional<User> optionalUser = this.userRepository.findByEmail(email);

        if (optionalUser.isEmpty()){
            model.addAttribute("error", "Invalid email or password. Try again!");
            return "login";
        }

        User user = optionalUser.get();
        String storedHash = user.getPassword();

        if (passwordEncoder.matches(rawPassword, storedHash)){
            return "home";
        }
        else{
            model.addAttribute("error", "Invalid email or password. Try again!");
            return "login";
        }

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
        return "login";
    }
}
