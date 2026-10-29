package util;

import model.Admin;
import model.Student;

/**
 * SessionManager — Tracks the currently logged-in user.
 * Static singleton: only one user can be logged in at a time.
 */
public class SessionManager {

    private static Student currentStudent;
    private static Admin   currentAdmin;

    private SessionManager() {}

    public static void setCurrentStudent(Student s) {
        currentStudent = s;
        currentAdmin   = null;
    }

    public static void setCurrentAdmin(Admin a) {
        currentAdmin   = a;
        currentStudent = null;
    }

    public static Student getCurrentStudent() { return currentStudent; }
    public static Admin   getCurrentAdmin()   { return currentAdmin; }

    public static boolean isStudentLoggedIn() { return currentStudent != null; }
    public static boolean isAdminLoggedIn()   { return currentAdmin   != null; }

    /** Clears session on logout. */
    public static void clear() {
        currentStudent = null;
        currentAdmin   = null;
    }
}
