package model;

/**
 * Admin — Concrete subclass of User representing an administrative staff member.
 * Demonstrates INHERITANCE and POLYMORPHISM.
 */
public class Admin extends User {

    private String adminLevel;   // e.g. "SUPER", "STUDENT_AFFAIRS", "COORDINATOR"

    public Admin() {
        super();
        this.role = "ADMIN";
        this.adminLevel = "SUPER";
    }

    public Admin(int id, String name, String email, String passwordHash, String salt, int hashIterations, String adminLevel) {
        super(id, name, email, passwordHash, salt, hashIterations, "ADMIN");
        this.adminLevel = adminLevel != null ? adminLevel : "SUPER";
    }

    @Override
    public String getDisplayInfo() {
        return name + " [" + adminLevel + " Admin]";
    }

    public String getAdminLevel()           { return adminLevel; }
    public void setAdminLevel(String lvl)   { this.adminLevel = lvl; }
}
