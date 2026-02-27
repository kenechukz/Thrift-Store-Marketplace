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

import com.example.marketplace.model.Admin;
import com.example.marketplace.repoository.UserRepository;
import com.example.marketplace.repoository.AdminRepository;
import com.example.marketplace.model.Basket;
import com.example.marketplace.model.PrincipalType;
import com.example.marketplace.model.User;
import com.example.marketplace.service.AdminAuthService;
import com.example.marketplace.service.AuthService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AuthService authService;
    private final AdminAuthService adminAuthService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, AdminRepository adminRepository, AuthService authService, AdminAuthService adminAuthService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.authService = authService;
        this.adminAuthService = adminAuthService;
    }


    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("principalType") == PrincipalType.ADMIN) {
            return "redirect:/admin";
        }
        if (session != null && session.getAttribute("principalType") == PrincipalType.USER){
            return "redirect:/home";
        }

        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(@ModelAttribute User loginForm,
                              @RequestParam(value = "roleType", required = false) String roleTypeValue,
                              @RequestParam(value = "roleTypeChoice", required = false) String roleTypeChoiceValue,
                              Model model,
                              HttpServletRequest request){
        PrincipalType roleType = resolveRoleType(roleTypeValue, roleTypeChoiceValue);
        if (roleType == null) {
            model.addAttribute("error", "Invalid role selected.");
            return "login";
        }

        String email = loginForm.getEmail();                                                                                                                                                                                             
        String rawPassword = loginForm.getPassword();

        HttpSession session = request.getSession();
        if (roleType == PrincipalType.USER) {
            Optional<User> optionalUser = this.authService.authenticate(email, rawPassword);
            if (optionalUser.isEmpty()) {
                model.addAttribute("error", "Invalid email or password. Try again!");
                return "login";
            }

            User user = optionalUser.get();
            session.setAttribute("principalType", PrincipalType.USER);
            session.setAttribute("principalId", user.getId());
            session.setAttribute("principalEmail", user.getEmail());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("basket", new Basket(user.getId()));
            return "redirect:/home";
        }

        Optional<Admin> optionalAdmin = this.adminAuthService.authenticate(email, rawPassword);
        if (optionalAdmin.isEmpty()) {
            model.addAttribute("error", "Invalid email or password. Try again!");
            return "login";
        }

        Admin admin = optionalAdmin.get();
        session.removeAttribute("basket");
        session.removeAttribute("userId");
        session.removeAttribute("userEmail");
        session.setAttribute("principalType", PrincipalType.ADMIN);
        session.setAttribute("principalId", admin.getId());
        session.setAttribute("principalEmail", admin.getEmail());
        
        return "redirect:/admin";
    }

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request){

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User loginForm,
                               @RequestParam("confirmedPassword") String confirmedPassword,
                               @RequestParam(value = "roleType", required = false) String roleTypeValue,
                               @RequestParam(value = "roleTypeChoice", required = false) String roleTypeChoiceValue,
                               Model model,
                               HttpServletRequest request){

        PrincipalType roleType = resolveRoleType(roleTypeValue, roleTypeChoiceValue);
        if (roleType == null) {
            model.addAttribute("error", "Invalid role selected.");
            return "register";
        }

        String email = loginForm.getEmail();
        String registeredPassword = loginForm.getPassword();

        if (!registeredPassword.equals(confirmedPassword)){
            model.addAttribute("error", "Passwords do not match, try again!");
            return "register";
        }

        if (roleType == PrincipalType.USER) {
            if (userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", "This email is taken. Please use a different email!");
                return "register";
            }
            if (adminRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", "This email is already used by an admin account.");
                return "register";
            }

            User newUser = new User(loginForm.getFirstName(), loginForm.getLastName(), email, passwordEncoder.encode(registeredPassword));
            userRepository.save(newUser);
            return "redirect:/login";
        }

        if (adminRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "This admin email is taken. Please use a different email!");
            return "register";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "This email is already used by a user account.");
            return "register";
        }

        Admin newAdmin = new Admin();
        newAdmin.setEmail(email);
        newAdmin.setPasswordHash(passwordEncoder.encode(registeredPassword));
        newAdmin.setCreatedAt(LocalDateTime.now());
        newAdmin.setEnabled(true);
        adminRepository.save(newAdmin);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest request){
        invalidateUserSession(request);

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String submitLogout(HttpServletRequest request) {
        invalidateUserSession(request);

        return "redirect:/login";
    }

    private void invalidateUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private PrincipalType parseRoleType(String roleTypeValue) {
        try {
            if (roleTypeValue == null) {
                return null;
            }
            return PrincipalType.valueOf(roleTypeValue.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    private PrincipalType resolveRoleType(String roleTypeValue, String roleTypeChoiceValue) {
        PrincipalType primary = parseRoleType(roleTypeValue);
        if (primary != null) {
            return primary;
        }
        return parseRoleType(roleTypeChoiceValue);
    }
}
