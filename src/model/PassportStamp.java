package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PassportStamp — One verified-attendance entry in a student's Campus Passport.
 */
public class PassportStamp {

    private int           id;
    private int           eventId;
    private String        eventTitle;
    private String        eventCategory;
    private String        opportunityType;
    private LocalDate     eventDate;
    private LocalDateTime verifiedAt;

    public PassportStamp() {}

    public PassportStamp(int id, int eventId, String eventTitle, String eventCategory,
                         String opportunityType, LocalDate eventDate, LocalDateTime verifiedAt) {
        this.id              = id;
        this.eventId         = eventId;
        this.eventTitle      = eventTitle;
        this.eventCategory   = eventCategory;
        this.opportunityType = opportunityType;
        this.eventDate       = eventDate;
        this.verifiedAt      = verifiedAt;
    }

    public int           getId()              { return id; }
    public int           getEventId()         { return eventId; }
    public String        getEventTitle()      { return eventTitle; }
    public String        getEventCategory()   { return eventCategory; }
    public String        getOpportunityType() { return opportunityType; }
    public LocalDate     getEventDate()       { return eventDate; }
    public LocalDateTime getVerifiedAt()      { return verifiedAt; }

    public void setId(int id)                              { this.id = id; }
    public void setEventId(int eventId)                    { this.eventId = eventId; }
    public void setEventTitle(String eventTitle)           { this.eventTitle = eventTitle; }
    public void setEventCategory(String eventCategory)     { this.eventCategory = eventCategory; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }
    public void setEventDate(LocalDate eventDate)          { this.eventDate = eventDate; }
    public void setVerifiedAt(LocalDateTime verifiedAt)    { this.verifiedAt = verifiedAt; }
}
