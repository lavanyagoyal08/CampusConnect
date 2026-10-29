package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registration — Tracks a student's registration for an event.
 */
public class Registration {

    // ── Status constants ──────────────────────────────────────────────────────
    public static final String STATUS_REGISTERED  = "REGISTERED";
    public static final String STATUS_CANCELLED   = "CANCELLED";

    public static final String ATTENDANCE_PENDING      = "PENDING";
    public static final String ATTENDANCE_ATTENDED     = "ATTENDED";
    public static final String ATTENDANCE_NOT_ATTENDED = "NOT_ATTENDED";

    private int           id;
    private int           studentId;
    private int           eventId;
    private String        status;            // REGISTERED | CANCELLED
    private String        attendanceStatus;  // PENDING | ATTENDED | NOT_ATTENDED
    private LocalDateTime registeredAt;
    private LocalDateTime verifiedAt;

    // Denormalised fields (populated by JOIN queries)
    private String    studentName;
    private String    eventTitle;
    private LocalDate eventDate;

    public Registration() {
        this.status           = STATUS_REGISTERED;
        this.attendanceStatus = ATTENDANCE_PENDING;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int           getId()                { return id; }
    public int           getStudentId()         { return studentId; }
    public int           getEventId()           { return eventId; }
    public String        getStatus()            { return status; }
    public String        getAttendanceStatus()  { return attendanceStatus; }
    public LocalDateTime getRegisteredAt()      { return registeredAt; }
    public LocalDateTime getVerifiedAt()        { return verifiedAt; }
    public String        getStudentName()       { return studentName; }
    public String        getEventTitle()        { return eventTitle; }
    public LocalDate     getEventDate()         { return eventDate; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setId(int id)                              { this.id = id; }
    public void setStudentId(int studentId)                { this.studentId = studentId; }
    public void setEventId(int eventId)                    { this.eventId = eventId; }
    public void setStatus(String status)                   { this.status = status; }
    public void setAttendanceStatus(String attendanceStatus){ this.attendanceStatus = attendanceStatus; }
    public void setRegisteredAt(LocalDateTime registeredAt){ this.registeredAt = registeredAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt)    { this.verifiedAt = verifiedAt; }
    public void setStudentName(String studentName)         { this.studentName = studentName; }
    public void setEventTitle(String eventTitle)           { this.eventTitle = eventTitle; }
    public void setEventDate(LocalDate eventDate)          { this.eventDate = eventDate; }
}
