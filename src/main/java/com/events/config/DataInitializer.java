package com.events.config;

import com.events.entity.Event;
import com.events.entity.User;
import com.events.repository.EventRepository;
import com.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;
import java.time.LocalDateTime;

@Component
public class DataInitializer {

    @Autowired private UserRepository userRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private boolean alreadyRun = false; // avoid multiple executions

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void init() {

        if (alreadyRun) return;
        alreadyRun = true;

        // Compte étudiant de démo
        if (!userRepo.existsByEmail("etudiant@universite.fr")) {
            User s = new User("Marie", "Dupont", "etudiant@universite.fr",
                    passwordEncoder.encode("Etudiant1"));
            userRepo.save(s);
        }

        // Compte admin de démo
        if (!userRepo.existsByEmail("admin@events.fr")) {
            User a = new User("Admin", "Portal", "admin@events.fr",
                    passwordEncoder.encode("Admin123!"));
            a.setRole("ROLE_ADMIN");
            userRepo.save(a);
        }

        // Événements
        if (eventRepo.count() == 0) {
            add("Conférence IA et Avenir du Travail",
                    "Interventions d'experts Google, Microsoft et startups locales.",
                    Event.EventType.CONFERENCE, 5, "Amphithéâtre A", 200, true);

            add("Atelier Spring MVC pour Débutants",
                    "Créez votre première application web Java en 3 heures.",
                    Event.EventType.WORKSHOP, 8, "Salle Informatique 204", 30, true);

            add("Soirée de Bienvenue des Nouveaux Étudiants",
                    "Grande soirée avec DJ, buffet et animations.",
                    Event.EventType.PARTY, 12, "Grande Salle des Fêtes", 500, false);

            add("Tournoi de Football Inter-Filières",
                    "Formez votre équipe et affrontez les autres filières !",
                    Event.EventType.SPORT, 15, "Terrain de Sport B", 120, true);

            add("Festival de Musique du Monde",
                    "Célébration de la diversité avec 12 pays représentés.",
                    Event.EventType.CULTURAL, 20, "Place Centrale du Campus", 1000, true);

            add("Hackathon Développement Durable 48h",
                    "48h pour concevoir des solutions tech pour l'environnement.",
                    Event.EventType.WORKSHOP, 25, "FabLab, Bâtiment Innovation", 80, true);

            add("Conférence Cybersécurité",
                    "Les menaces numériques et comment s'en protéger.",
                    Event.EventType.CONFERENCE, 30, "Salle C101", 150, true);

            add("Tournoi de Tennis de Table",
                    "Championnat étudiant. Tous niveaux bienvenus.",
                    Event.EventType.SPORT, 10, "Gymnase Principal", 64, true);
        }
    }

    private void add(String title, String desc, Event.EventType type,
                     int days, String loc, int cap, boolean free) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(desc);
        e.setType(type);
        e.setEventDate(LocalDateTime.now().plusDays(days));
        e.setLocation(loc);
        e.setCapacity(cap);
        e.setFree(free);
        eventRepo.save(e);
    }
}