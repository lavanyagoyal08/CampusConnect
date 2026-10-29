package service;

import dao.RegistrationDAO;
import dao.SavedEventDAO;
import model.Event;
import model.Registration;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistrationService — High-level event registration business logic.
 * Delegates safe (concurrent) insert to RegistrationDAO.safeRegister.
 */
public class RegistrationService {

    private final RegistrationDAO regDAO   = new RegistrationDAO();
    private final SavedEventDAO   savedDAO = new SavedEventDAO();

    /**
     * Registers a student for an event via the stored procedure.
     * @return a user-facing result message
     */
    public String registerForEvent(int studentId, int eventId) throws SQLException {
        return regDAO.safeRegister(studentId, eventId);
    }

    /**
     * Cancels an active registration.
     * @return true if a registration was found and cancelled
     */
    public boolean cancelRegistration(int studentId, int eventId) throws SQLException {
        return regDAO.cancelRegistration(studentId, eventId);
    }

    /** Returns the current registration record, or null if not registered. */
    public Registration getRegistration(int studentId, int eventId) throws SQLException {
        return regDAO.findByStudentAndEvent(studentId, eventId);
    }

    /** Returns all registrations for a student (active + cancelled). */
    public List<Registration> getMyRegistrations(int studentId) throws SQLException {
        return regDAO.findByStudent(studentId);
    }

    // ── Saved events ──────────────────────────────────────────────────────────

    public void saveEvent(int studentId, int eventId) throws SQLException {
        savedDAO.saveEvent(studentId, eventId);
    }

    public void unsaveEvent(int studentId, int eventId) throws SQLException {
        savedDAO.unsaveEvent(studentId, eventId);
    }

    public boolean isEventSaved(int studentId, int eventId) throws SQLException {
        return savedDAO.isEventSaved(studentId, eventId);
    }

    public List<Event> getSavedEvents(int studentId) throws SQLException {
        return savedDAO.findSavedEventsByStudent(studentId);
    }
}
