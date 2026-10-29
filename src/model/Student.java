package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Student — Concrete subclass of User representing a campus student.
 * Demonstrates INHERITANCE, POLYMORPHISM, and ENCAPSULATION.
 */
public class Student extends User {

    private String       department;
    private int          yearOfStudy;
    private List<String> interests = new ArrayList<>();

    public Student() {
        super();
        this.role = "STUDENT";
    }

    public Student(int id, String name, String email, String passwordHash, String salt, int hashIterations,
                   String department, int yearOfStudy, List<String> interests) {
        super(id, name, email, passwordHash, salt, hashIterations, "STUDENT");
        this.department  = department;
        this.yearOfStudy = yearOfStudy;
        if (interests != null) this.interests = new ArrayList<>(interests);
    }

    @Override
    public String getDisplayInfo() {
        return name + " · " + getYearDeptLabel();
    }

    /** Returns formatted label, e.g. "Year 2 · Computer Science" */
    public String getYearDeptLabel() {
        return "Year " + yearOfStudy + " · " + (department == null || department.isBlank() ? "General" : department);
    }

    /** Returns comma-delimited interests string for compact database persistence */
    public String getInterestsDelimited() {
        if (interests == null || interests.isEmpty()) return "";
        return String.join(",", interests);
    }

    /** Parses comma-delimited interests string from database */
    public void setInterestsFromDelimited(String delimited) {
        interests = new ArrayList<>();
        if (delimited != null && !delimited.isBlank()) {
            String[] parts = delimited.split(",");
            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    interests.add(trimmed);
                }
            }
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public String getDepartment()              { return department; }
    public void setDepartment(String d)        { this.department = d; }

    public int getYearOfStudy()                { return yearOfStudy; }
    public void setYearOfStudy(int y)          { this.yearOfStudy = y; }

    public List<String> getInterests()         { return interests; }
    public void setInterests(List<String> i)   { this.interests = (i != null) ? new ArrayList<>(i) : new ArrayList<>(); }
}
