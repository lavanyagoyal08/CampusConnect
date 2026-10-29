package model;

/**
 * Club — Represents a college club.
 */
public class Club {

    private int     id;
    private String  name;
    private String  category;
    private String  description;
    private String  contactEmail;
    private String  contactInfo;
    private boolean active;

    public Club() {}

    public Club(int id, String name, String category, String description,
                String contactEmail, String contactInfo, boolean active) {
        this.id           = id;
        this.name         = name;
        this.category     = category;
        this.description  = description;
        this.contactEmail = contactEmail;
        this.contactInfo  = contactInfo;
        this.active       = active;
    }

    public int     getId()              { return id; }
    public void    setId(int id)        { this.id = id; }

    public String  getName()            { return name; }
    public void    setName(String n)    { this.name = n; }

    public String  getCategory()        { return category; }
    public void    setCategory(String c){ this.category = c; }

    public String  getDescription()           { return description; }
    public void    setDescription(String d)   { this.description = d; }

    public String  getContactEmail()          { return contactEmail; }
    public void    setContactEmail(String e)  { this.contactEmail = e; }

    public String  getContactInfo()           { return contactInfo; }
    public void    setContactInfo(String ci)  { this.contactInfo = ci; }

    public boolean isActive()           { return active; }
    public void    setActive(boolean a) { this.active = a; }

    @Override public String toString() { return name + " (" + category + ")"; }
}
