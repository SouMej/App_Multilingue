package com.events.controller;

import com.events.entity.Event;
import com.events.service.EventService;
import com.events.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur des événements — ACCÈS PROTÉGÉ.
 * Spring Security redirige automatiquement vers /login
 * si l'utilisateur n'est pas connecté (configuré dans SecurityConfig).
 */
@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired private EventService eventService;
    @Autowired private UserService  userService;

    @GetMapping
    public String list(
            @RequestParam(value = "q",    required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            Model model) {

        // Récupère l'utilisateur connecté depuis la session Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            model.addAttribute("currentUser", userService.findByEmail(auth.getName()));
        } catch (Exception ignored) {}

        boolean isSearch = (keyword != null && !keyword.isBlank())
                        || (type    != null && !type.isBlank());

        List<Event> events = isSearch
                ? eventService.search(keyword, type)
                : eventService.getAll();

        model.addAttribute("events",       events);
        model.addAttribute("eventTypes",   Event.EventType.values());
        model.addAttribute("keyword",      keyword);
        model.addAttribute("selectedType", type);
        model.addAttribute("isSearch",     isSearch);
        model.addAttribute("resultCount",  events.size());

        return "events/list"; // -> /WEB-INF/views/events/list.html
    }
}
