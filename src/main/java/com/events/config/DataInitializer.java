package com.events.config;

import com.events.entity.Event;
import com.events.entity.EventTranslation;
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
import java.util.EnumMap;
import java.util.Map;

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

        ensureTranslationsForAllEvents();
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
        applySeedTranslations(e);
        eventRepo.save(e);
    }

    private void ensureTranslationsForAllEvents() {
        for (Event event : eventRepo.findAll()) {
            applySeedTranslations(event);
        }
    }

    private void applySeedTranslations(Event event) {
        Map<EventTranslation.Language, TranslationText> texts = translationsFor(event);
        ensureTranslation(event, EventTranslation.Language.FR, texts.get(EventTranslation.Language.FR));
        ensureTranslation(event, EventTranslation.Language.EN, texts.get(EventTranslation.Language.EN));
        ensureTranslation(event, EventTranslation.Language.AR, texts.get(EventTranslation.Language.AR));
    }

    private void ensureTranslation(Event event, EventTranslation.Language language, TranslationText text) {
        for (EventTranslation translation : event.getTranslations()) {
            if (translation.getLanguage() == language) return;
        }
        event.addTranslation(new EventTranslation(language, text.title(), text.description()));
    }

    private Map<EventTranslation.Language, TranslationText> translationsFor(Event event) {
        String title = event.getTitle() != null ? event.getTitle() : "";
        String description = event.getDescription() != null ? event.getDescription() : "";
        Map<EventTranslation.Language, TranslationText> fallback = defaultTranslations(title, description);

        if ("Conférence IA et Avenir du Travail".equals(title)) {
            return of(
                "Conférence IA et Avenir du Travail",
                "Interventions d'experts Google, Microsoft et startups locales.",
                "AI Conference and the Future of Work",
                "Talks from experts at Google, Microsoft, and local startups.",
                "مؤتمر الذكاء الاصطناعي ومستقبل العمل",
                "مداخلات من خبراء Google وMicrosoft والشركات الناشئة المحلية."
            );
        }
        if ("Atelier Spring MVC pour Débutants".equals(title)) {
            return of(
                "Atelier Spring MVC pour Débutants",
                "Créez votre première application web Java en 3 heures.",
                "Spring MVC Workshop for Beginners",
                "Build your first Java web application in 3 hours.",
                "ورشة Spring MVC للمبتدئين",
                "أنشئ أول تطبيق ويب Java خلال 3 ساعات."
            );
        }
        if ("Soirée de Bienvenue des Nouveaux Étudiants".equals(title)) {
            return of(
                "Soirée de Bienvenue des Nouveaux Étudiants",
                "Grande soirée avec DJ, buffet et animations.",
                "Welcome Party for New Students",
                "Big party with DJ, buffet, and entertainment.",
                "حفل استقبال الطلاب الجدد",
                "سهرة كبيرة مع دي جي وبوفيه وأنشطة ترفيهية."
            );
        }
        if ("Tournoi de Football Inter-Filières".equals(title)) {
            return of(
                "Tournoi de Football Inter-Filières",
                "Formez votre équipe et affrontez les autres filières !",
                "Inter-Department Football Tournament",
                "Form your team and challenge other departments!",
                "دوري كرة القدم بين التخصصات",
                "كوّن فريقك وتحدَّ التخصصات الأخرى!"
            );
        }
        if ("Festival de Musique du Monde".equals(title)) {
            return of(
                "Festival de Musique du Monde",
                "Célébration de la diversité avec 12 pays représentés.",
                "World Music Festival",
                "Celebrating diversity with 12 countries represented.",
                "مهرجان موسيقى العالم",
                "احتفال بالتنوع مع تمثيل 12 دولة."
            );
        }
        if ("Hackathon Développement Durable 48h".equals(title)) {
            return of(
                "Hackathon Développement Durable 48h",
                "48h pour concevoir des solutions tech pour l'environnement.",
                "48h Sustainable Development Hackathon",
                "48 hours to design tech solutions for the environment.",
                "هاكاثون التنمية المستدامة 48 ساعة",
                "48 ساعة لتصميم حلول تقنية من أجل البيئة."
            );
        }
        if ("Conférence Cybersécurité".equals(title)) {
            return of(
                "Conférence Cybersécurité",
                "Les menaces numériques et comment s'en protéger.",
                "Cybersecurity Conference",
                "Digital threats and how to protect yourself.",
                "مؤتمر الأمن السيبراني",
                "التهديدات الرقمية وكيفية الحماية منها."
            );
        }
        if ("Tournoi de Tennis de Table".equals(title)) {
            return of(
                "Tournoi de Tennis de Table",
                "Championnat étudiant. Tous niveaux bienvenus.",
                "Table Tennis Tournament",
                "Student championship. All skill levels are welcome.",
                "بطولة تنس الطاولة",
                "بطولة للطلاب. جميع المستويات مرحب بها."
            );
        }

        return fallback;
    }

    private Map<EventTranslation.Language, TranslationText> defaultTranslations(String title, String description) {
        return of(title, description, title, description, title, description);
    }

    private Map<EventTranslation.Language, TranslationText> of(
            String frTitle, String frDesc, String enTitle, String enDesc, String arTitle, String arDesc) {
        Map<EventTranslation.Language, TranslationText> map = new EnumMap<>(EventTranslation.Language.class);
        map.put(EventTranslation.Language.FR, new TranslationText(frTitle, frDesc));
        map.put(EventTranslation.Language.EN, new TranslationText(enTitle, enDesc));
        map.put(EventTranslation.Language.AR, new TranslationText(arTitle, arDesc));
        return map;
    }

    private record TranslationText(String title, String description) {}
}
