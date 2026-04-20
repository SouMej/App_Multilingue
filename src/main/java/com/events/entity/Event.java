package com.events.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
}
