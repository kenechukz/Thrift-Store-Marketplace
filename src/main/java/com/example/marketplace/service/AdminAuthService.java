package com.example.marketplace.service;

import com.example.marketplace.model.Admin;
import com.example.marketplace.repoository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Optional<Admin> authenticate(String email, String rawPassword) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isEmpty()) {
            return Optional.empty();
        }

        Admin admin = optionalAdmin.get();
        if (!admin.isEnabled()) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(rawPassword, admin.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(admin);
    }
}
