package com.events.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitaires — SecurityConfig")
class SecurityConfigTest {

    @Test
    @DisplayName("Injection UserDetailsService par interface")
    void shouldInjectUserDetailsServiceInterface() throws NoSuchFieldException {
        Field field = SecurityConfig.class.getDeclaredField("userDetailsService");
        assertThat(field.getType()).isEqualTo(UserDetailsService.class);
    }
}
