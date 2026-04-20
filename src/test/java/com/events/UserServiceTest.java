package com.events;

import com.events.entity.UserRegistrationDto;
import com.events.repository.UserRepository;
import com.events.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — UserService")
class UserServiceTest {

    @Mock UserRepository  userRepo;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    private UserRegistrationDto valid;

    @BeforeEach void setUp() {
        valid = new UserRegistrationDto();
        valid.setFirstName("Marie"); valid.setLastName("Dupont");
        valid.setEmail("marie@univ.fr");
        valid.setPassword("Secure1!"); valid.setConfirmPassword("Secure1!");
    }

    @Test @DisplayName("Inscription OK")
    void register_ok() {
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$hashed");
        userService.register(valid);
        verify(userRepo).save(argThat(u -> "marie@univ.fr".equals(u.getEmail())));
        verify(passwordEncoder).encode("Secure1!");
    }

    @Test @DisplayName("Email déjà utilisé")
    void register_emailExists() {
        when(userRepo.existsByEmail(anyString())).thenReturn(true);
        assertThatThrownBy(() -> userService.register(valid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("register.error.email.exists");
        verify(userRepo, never()).save(any());
    }

    @Test @DisplayName("Mots de passe différents")
    void register_pwdMismatch() {
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        valid.setConfirmPassword("Autre1!");
        assertThatThrownBy(() -> userService.register(valid))
            .hasMessageContaining("register.error.password.mismatch");
    }

    @Test @DisplayName("Mot de passe trop faible")
    void register_weakPwd() {
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        valid.setPassword("faible"); valid.setConfirmPassword("faible");
        assertThatThrownBy(() -> userService.register(valid))
            .hasMessageContaining("register.error.password.weak");
    }
}
