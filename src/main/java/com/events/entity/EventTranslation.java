package com.events.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "event_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "lang"})
)
public class EventTranslation {

    public enum Language { FR, EN, AR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", nullable = false, length = 5)
    private Language language;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public EventTranslation() {}

    public EventTranslation(Language language, String title, String description) {
        this.language = language;
        this.title = title;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
