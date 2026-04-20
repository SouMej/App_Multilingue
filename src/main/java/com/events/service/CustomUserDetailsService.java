package com.events.service;

import com.events.entity.User;
import com.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Implémentation de UserDetailsService.
 * Spring Security appelle loadUserByUsername() lors de la connexion
 * pour charger l'utilisateur depuis MySQL et vérifier le mot de passe BCrypt.
 *
 * Flux de session :
 * 1. POST /login  → Spring Security appelle loadUserByUsername(email)
 * 2. Compare le mot de passe saisi avec le hash BCrypt en base
 * 3. Si OK : crée une session HTTP + cookie JSESSIONID
 * 4. À chaque requête : le cookie identifie la session → utilisateur reconnu
 */
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Non trouvé : " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
