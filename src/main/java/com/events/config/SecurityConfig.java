package com.events.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * CONFIGURATION SPRING SECURITY 6 — Pure Java, zéro XML.
 *
 * Remplace l'ancien spring-security.xml.
 * Gère :
 *  - Quelles URLs sont publiques ou protégées
 *  - Le formulaire de connexion personnalisé
 *  - La gestion de session (cookie Remember Me)
 *  - BCrypt pour hacher les mots de passe
 *  - La déconnexion
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * BCrypt force 12 = bon équilibre sécurité / performance.
     * Impossible de retrouver le mot de passe d'origine depuis le hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Relie notre service utilisateur à l'encodeur BCrypt.
     * C'est ce provider qui vérifie email + password lors de la connexion.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Chaîne de filtres de sécurité HTTP.
     * Définit toutes les règles : accès, login, logout, session.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
            /* ---- AUTORISATIONS ---- */
            .authorizeHttpRequests(auth -> auth
                // Pages publiques
                .requestMatchers("/", "/home", "/register", "/login",
                                 "/resources/**").permitAll()
                // Tout le reste exige une connexion
                .anyRequest().authenticated()
            )

            /* ---- FORMULAIRE DE CONNEXION ---- */
            .formLogin(form -> form
                .loginPage("/login")               // Notre page JSP/HTML
                .loginProcessingUrl("/login")       // URL traitée par Spring Security
                .usernameParameter("email")          // Champ "email" comme identifiant
                .passwordParameter("password")
                .defaultSuccessUrl("/events", true)  // Après connexion OK
                .failureUrl("/login?error=true")     // Après échec
                .permitAll()
            )

            /* ---- DÉCONNEXION ---- */
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .clearAuthentication(true)
                .permitAll()
            )

            /* ---- REMEMBER ME (Se souvenir de moi — 7 jours) ---- */
            .rememberMe(rm -> rm
                .key("events-portal-secret-2024")
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .rememberMeParameter("rememberMe")
            );

        return http.build();
    }
}
