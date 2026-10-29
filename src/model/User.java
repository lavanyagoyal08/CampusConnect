package model;

/**
 * User — Abstract base class representing any authenticated system actor.
 * Demonstrates OOP INHERITANCE and ABSTRACTION.
 */
public abstract class User {

    protected int    id;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String salt;
    protected int    hashIterations;
    protected String role;   // "STUDENT" | "ADMIN"

    public User() {}

    public User(int id, String name, String email, String passwordHash, String salt, int hashIterations, String role) {
        this.id             = id;
        this.name           = name;
        this.email          = email;
        this.passwordHash   = passwordHash;
        this.salt           = salt;
        this.hashIterations = hashIterations;
        this.role           = role;
    }

    // ── Abstract method demonstrating POLYMORPHISM in subclasses ───────────────
    public abstract String getDisplayInfo();

    // ── Getters & Setters (ENCAPSULATION) ──────────────────────────────────────
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail()         { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash()  { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt()          { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public int getHashIterations()   { return hashIterations; }
    public void setHashIterations(int hashIterations) { this.hashIterations = hashIterations; }

    public String getRole()          { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
