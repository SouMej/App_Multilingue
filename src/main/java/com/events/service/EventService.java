package com.events.service;

import com.events.entity.Event;
import com.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepo;

    @Transactional(readOnly = true)
    public List<Event> getAll() {
        return eventRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> search(String keyword, String typeStr) {
        String kw = (keyword != null) ? keyword.trim() : null;
        Event.EventType type = null;
        if (typeStr != null && !typeStr.isBlank()) {
            try { type = Event.EventType.valueOf(typeStr.toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }
        return eventRepo.search((kw != null && kw.isBlank()) ? null : kw, type);
    }
}
