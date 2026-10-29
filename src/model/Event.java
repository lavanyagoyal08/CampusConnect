package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Event — Represents any campus activity: workshop, hackathon, competition, etc.
 */
public class Event {

    private int           id;
    private String        title;
    private String        description;
    private String        category;          // Technology, Cultural, Sports, etc.
    private String        opportunityType;   // Workshop, Hackathon, Competition, etc.
    private LocalDate     eventDate;
    private String        venue;
    private String        organizer;
    private LocalDateTime regDeadline;
    private String        eligibility;
    private int           capacity;
    private int           availableSeats;
    private boolean       registrationOpen;
    private boolean       active;

    public Event() {}

    // ── Getters ───────────────────────────────────────────────────────────────
    public int           getId()                { return id; }
    public String        getTitle()             { return title; }
    public String        getDescription()       { return description; }
    public String        getCategory()          { return category; }
    public String        getOpportunityType()   { return opportunityType; }
    public LocalDate     getEventDate()         { return eventDate; }
    public String        getVenue()             { return venue; }
    public String        getOrganizer()         { return organizer; }
    public LocalDateTime getRegDeadline()       { return regDeadline; }
    public String        getEligibility()       { return eligibility; }
    public int           getCapacity()          { return capacity; }
    public int           getAvailableSeats()    { return availableSeats; }
    public boolean       isRegistrationOpen()   { return registrationOpen; }
    public boolean       isActive()             { return active; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(int id)                          { this.id = id; }
    public void setTitle(String title)                 { this.title = title; }
    public void setDescription(String description)     { this.description = description; }
    public void setCategory(String category)           { this.category = category; }
    public void setOpportunityType(String t)           { this.opportunityType = t; }
    public void setEventDate(LocalDate eventDate)      { this.eventDate = eventDate; }
    public void setVenue(String venue)                 { this.venue = venue; }
    public void setOrganizer(String organizer)         { this.organizer = organizer; }
    public void setRegDeadline(LocalDateTime dl)       { this.regDeadline = dl; }
    public void setEligibility(String eligibility)     { this.eligibility = eligibility; }
    public void setCapacity(int capacity)              { this.capacity = capacity; }
    public void setAvailableSeats(int availableSeats)  { this.availableSeats = availableSeats; }
    public void setRegistrationOpen(boolean b)         { this.registrationOpen = b; }
    public void setActive(boolean active)              { this.active = active; }

    @Override public String toString() { return title + " [" + category + "]"; }
}
