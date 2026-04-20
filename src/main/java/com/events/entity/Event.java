package com.events.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(length = 200)
    private String location;

    private Integer capacity;

    @Column(nullable = false)
    private boolean free = true;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EventTranslation> translations = new ArrayList<>();

    public enum EventType { CONFERENCE, WORKSHOP, PARTY, SPORT, CULTURAL }

    // Constructeurs
    public Event() {}

    // Getters / Setters
    public Long          getId()          { return id; }
    public void          setId(Long v)    { this.id = v; }
    public String        getTitle()       { return title; }
    public void          setTitle(String v)     { this.title = v; }
    public String        getDescription() { return description; }
    public void          setDescription(String v) { this.description = v; }
    public EventType     getType()        { return type; }
    public void          setType(EventType v)   { this.type = v; }
    public LocalDateTime getEventDate()   { return eventDate; }
    public void          setEventDate(LocalDateTime v) { this.eventDate = v; }
    public String        getLocation()    { return location; }
    public void          setLocation(String v)  { this.location = v; }
    public Integer       getCapacity()    { return capacity; }
    public void          setCapacity(Integer v) { this.capacity = v; }
    public boolean       isFree()         { return free; }
    public void          setFree(boolean v)     { this.free = v; }
    public List<EventTranslation> getTranslations() { return translations; }
    public void setTranslations(List<EventTranslation> translations) { this.translations = translations; }

    public void addTranslation(EventTranslation translation) {
        if (translation == null) return;
        translation.setEvent(this);
        this.translations.add(translation);
    }

    public EventTranslation getTranslation(String localeLanguage) {
        EventTranslation.Language requested;
        if (localeLanguage == null || localeLanguage.isBlank()) {
            requested = EventTranslation.Language.FR;
        } else {
            try {
                requested = EventTranslation.Language.valueOf(localeLanguage.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                requested = EventTranslation.Language.FR;
            }
        }

        for (EventTranslation translation : translations) {
            if (translation.getLanguage() == requested) return translation;
        }
        for (EventTranslation translation : translations) {
            if (translation.getLanguage() == EventTranslation.Language.FR) return translation;
        }
        return null;
    }
}
