package com.events.service;

import com.events.entity.User;
import com.events.entity.UserRegistrationDto;
import com.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserRegistrationDto dto) {

        if (userRepo.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("register.error.email.exists");

        if (!dto.passwordsMatch())
            throw new IllegalArgumentException("register.error.password.mismatch");

        if (!dto.isPasswordStrong())
            throw new IllegalArgumentException("register.error.password.weak");

        User user = new User(
            dto.getFirstName().trim(),
            dto.getLastName().trim(),
            dto.getEmail().toLowerCase().trim(),
            passwordEncoder.encode(dto.getPassword())   // BCrypt — jamais en clair
        );
        userRepo.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + email));
    }
}
