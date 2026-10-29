package service;

import dao.RegistrationDAO;
import model.PassportStamp;
import model.Registration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PassportService — Feature 3: Campus Passport.
 * Derives stamps and badges entirely from verified attendance records.
 */
public class PassportService {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    public static final String BADGE_FIRST_EVENT  = "🥇 First Event";
    public static final String BADGE_TECH_EXPLORER = "💻 Tech Explorer";
    public static final String BADGE_COMMUNITY     = "🤝 Community Contributor";
    public static final String BADGE_CREATIVE      = "🎨 Creative Mind";
    public static final String BADGE_ALL_ROUNDER   = "⭐ All Rounder";

    private static final int TECH_THRESHOLD      = 3;
    private static final int COMMUNITY_THRESHOLD = 2;
    private static final int CREATIVE_THRESHOLD  = 2;
    private static final int ALL_ROUNDER_THRESHOLD = 5;

    public List<PassportStamp> getStampsWithCategory(int studentId) throws SQLException {
        dao.EventDAO eventDAO = new dao.EventDAO();
        List<Registration> attended = registrationDAO.findAttendedByStudent(studentId);
        List<PassportStamp> stamps = new ArrayList<>();
        for (Registration reg : attended) {
            model.Event event = eventDAO.findById(reg.getEventId());
            if (event == null) continue;
            stamps.add(new PassportStamp(
                reg.getId(), event.getId(), event.getTitle(),
                event.getCategory(), event.getOpportunityType(),
                event.getEventDate(), reg.getVerifiedAt()
            ));
        }
        return stamps;
    }

    public List<String> computeBadgesFromStamps(List<PassportStamp> stamps) {
        List<String> badges = new ArrayList<>();
        if (stamps.isEmpty()) return badges;

        badges.add(BADGE_FIRST_EVENT);

        long tech = stamps.stream().filter(s -> "Technology".equals(s.getEventCategory())).count();
        if (tech >= TECH_THRESHOLD) badges.add(BADGE_TECH_EXPLORER);

        long community = stamps.stream().filter(s -> "Social Impact".equals(s.getEventCategory())).count();
        if (community >= COMMUNITY_THRESHOLD) badges.add(BADGE_COMMUNITY);

        long creative = stamps.stream()
                .filter(s -> "Cultural".equals(s.getEventCategory()) || "Design".equals(s.getEventCategory()))
                .count();
        if (creative >= CREATIVE_THRESHOLD) badges.add(BADGE_CREATIVE);

        if (stamps.size() >= ALL_ROUNDER_THRESHOLD) badges.add(BADGE_ALL_ROUNDER);
        return badges;
    }

    public int getTotalVerifiedCount(int studentId) throws SQLException {
        return registrationDAO.findAttendedByStudent(studentId).size();
    }
}
