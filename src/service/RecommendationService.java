package service;

import dao.ClubDAO;
import dao.EventDAO;
import dao.StudentDAO;
import model.Club;
import model.Event;
import model.Student;

import java.sql.SQLException;
import java.util.*;

/**
 * RecommendationService — Feature 1: Opportunity Match.
 * Rule-based recommendation engine matching student interests with club and event categories.
 * Ranks results by match score and event date.
 */
public class RecommendationService {

    private final EventDAO   eventDAO   = new EventDAO();
    private final ClubDAO    clubDAO    = new ClubDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    private static final Map<String, List<String>> INTEREST_CATEGORY_MAP = new LinkedHashMap<>();

    static {
        INTEREST_CATEGORY_MAP.put("Programming",             Arrays.asList("Technology"));
        INTEREST_CATEGORY_MAP.put("Artificial Intelligence", Arrays.asList("Technology"));
        INTEREST_CATEGORY_MAP.put("Data Science",            Arrays.asList("Technology"));
        INTEREST_CATEGORY_MAP.put("Design",                  Arrays.asList("Design"));
        INTEREST_CATEGORY_MAP.put("Entrepreneurship",        Arrays.asList("Entrepreneurship"));
        INTEREST_CATEGORY_MAP.put("Cultural Activities",     Arrays.asList("Cultural"));
        INTEREST_CATEGORY_MAP.put("Sports",                  Arrays.asList("Sports"));
        INTEREST_CATEGORY_MAP.put("Volunteering",            Arrays.asList("Social Impact"));
        INTEREST_CATEGORY_MAP.put("Public Speaking",         Arrays.asList("Entrepreneurship", "Cultural"));
        INTEREST_CATEGORY_MAP.put("Social Impact",           Arrays.asList("Social Impact"));
    }

    /**
     * Recommends upcoming events matching the student's selected interests.
     */
    public List<RecommendedEvent> getRecommendedEvents(Student student) throws SQLException {
        if (student == null) return Collections.emptyList();
        List<String> interests = student.getInterests();
        if (interests == null || interests.isEmpty()) return Collections.emptyList();

        Set<String> matchedCategories = new LinkedHashSet<>();
        for (String interest : interests) {
            matchedCategories.addAll(INTEREST_CATEGORY_MAP.getOrDefault(interest, Collections.emptyList()));
        }

        List<Event> eligibleEvents = eventDAO.findByCategories(new ArrayList<>(matchedCategories));
        List<RecommendedEvent> results = new ArrayList<>();

        for (Event ev : eligibleEvents) {
            int score = 0;
            List<String> matchedInterests = new ArrayList<>();
            for (String interest : interests) {
                List<String> cats = INTEREST_CATEGORY_MAP.getOrDefault(interest, Collections.emptyList());
                if (cats.contains(ev.getCategory())) {
                    score++;
                    matchedInterests.add(interest);
                }
            }
            if (score > 0) {
                results.add(new RecommendedEvent(ev, score, buildReason(matchedInterests)));
            }
        }

        // Rank primarily by match score descending, then by upcoming event date ascending
        results.sort(Comparator.comparingInt(RecommendedEvent::getScore).reversed()
                               .thenComparing(r -> r.getEvent().getEventDate()));
        return results;
    }

    public List<RecommendedEvent> getRecommendedEvents(int studentId) throws SQLException {
        Student s = studentDAO.findById(studentId);
        return getRecommendedEvents(s);
    }

    /**
     * Recommends campus clubs matching the student's selected interests.
     */
    public List<RecommendedClub> getRecommendedClubs(Student student) throws SQLException {
        if (student == null) return Collections.emptyList();
        List<String> interests = student.getInterests();
        if (interests == null || interests.isEmpty()) return Collections.emptyList();

        List<Club> allClubs = clubDAO.findAll();
        List<RecommendedClub> results = new ArrayList<>();

        for (Club club : allClubs) {
            int score = 0;
            List<String> matchedInterests = new ArrayList<>();
            for (String interest : interests) {
                List<String> cats = INTEREST_CATEGORY_MAP.getOrDefault(interest, Collections.emptyList());
                if (cats.contains(club.getCategory())) {
                    score++;
                    matchedInterests.add(interest);
                }
            }
            if (score > 0) {
                results.add(new RecommendedClub(club, score, buildReason(matchedInterests)));
            }
        }

        results.sort(Comparator.comparingInt(RecommendedClub::getScore).reversed());
        return results;
    }

    public List<RecommendedClub> getRecommendedClubs(int studentId) throws SQLException {
        Student s = studentDAO.findById(studentId);
        return getRecommendedClubs(s);
    }

    public static List<String> getAllInterestLabels() {
        return new ArrayList<>(INTEREST_CATEGORY_MAP.keySet());
    }

    private String buildReason(List<String> matched) {
        if (matched.isEmpty()) return "";
        if (matched.size() == 1) return "Matches your interest in " + matched.get(0);
        return "Matches your interests in " + String.join(" & ", matched);
    }

    // ── Value objects ─────────────────────────────────────────────────────────

    public static class RecommendedEvent {
        private final Event event;
        private final int score;
        private final String reason;

        public RecommendedEvent(Event event, int score, String reason) {
            this.event = event;
            this.score = score;
            this.reason = reason;
        }

        public Event getEvent()     { return event; }
        public int getScore()       { return score; }
        public String getReason()   { return reason; }
    }

    public static class RecommendedClub {
        private final Club club;
        private final int score;
        private final String reason;

        public RecommendedClub(Club club, int score, String reason) {
            this.club = club;
            this.score = score;
            this.reason = reason;
        }

        public Club getClub()       { return club; }
        public int getScore()       { return score; }
        public String getReason()   { return reason; }
    }
}
