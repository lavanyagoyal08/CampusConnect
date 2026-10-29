package service;

import dao.AdminDAO;
import dao.StudentDAO;
import model.Admin;
import model.Student;
import util.PasswordUtil;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.Collections;

/**
 * AuthService — Handles student and admin registration and authentication.
 * Uses PBKDF2WithHmacSHA256 password hashing via PasswordUtil.
 */
public class AuthService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AdminDAO   adminDAO   = new AdminDAO();

    // ── Student Authentication ────────────────────────────────────────────────

    /**
     * Registers a new student. Validates inputs, checks for duplicate email,
     * hashes password using PBKDF2 with salt, and saves to database.
     */
    public Student registerStudent(String name, String email, String password,
                                   String confirmPassword, String department, int yearOfStudy)
            throws AuthException, SQLException {

        check(ValidationUtil.validateName(name));
        check(ValidationUtil.validateEmail(email));
        check(ValidationUtil.validatePassword(password));
        check(ValidationUtil.validatePasswordMatch(password, confirmPassword));
        check(ValidationUtil.validateNotBlank(department, "Department"));

        String cleanEmail = email.trim().toLowerCase();
        if (studentDAO.emailExists(cleanEmail)) {
            throw new AuthException("An account with this email address already exists.");
        }

        PasswordUtil.HashResult hashResult = PasswordUtil.hashPassword(password);

        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(cleanEmail);
        student.setPasswordHash(hashResult.getHashBase64());
        student.setSalt(hashResult.getSaltBase64());
        student.setHashIterations(hashResult.getIterations());
        student.setDepartment(department.trim());
        student.setYearOfStudy(yearOfStudy);
        student.setInterests(Collections.emptyList());

        int id = studentDAO.insert(student);
        student.setId(id);
        return student;
    }

    /**
     * Authenticates a student using email and password with constant-time verification.
     */
    public Student loginStudent(String email, String password) throws AuthException, SQLException {
        check(ValidationUtil.validateEmail(email));
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password is required.");
        }

        Student student = studentDAO.findByEmail(email.trim().toLowerCase());
        if (student == null) {
            throw new AuthException("Invalid email or password.");
        }

        boolean valid = PasswordUtil.verify(
            password,
            student.getPasswordHash(),
            student.getSalt(),
            student.getHashIterations()
        );

        // Fallback for combined salt:hash strings if legacy format exists
        if (!valid && student.getPasswordHash() != null && student.getPasswordHash().contains(":")) {
            valid = PasswordUtil.verify(password, student.getPasswordHash());
        }

        if (!valid) {
            throw new AuthException("Invalid email or password.");
        }

        return student;
    }

    // ── Admin Authentication ──────────────────────────────────────────────────

    /**
     * Authenticates an administrator.
     */
    public Admin loginAdmin(String email, String password) throws AuthException, SQLException {
        if (email == null || email.isBlank()) {
            throw new AuthException("Email is required.");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password is required.");
        }

        Admin admin = adminDAO.findByEmail(email.trim().toLowerCase());
        if (admin == null) {
            throw new AuthException("Invalid admin credentials.");
        }

        boolean valid = PasswordUtil.verify(
            password,
            admin.getPasswordHash(),
            admin.getSalt(),
            admin.getHashIterations()
        );

        if (!valid && admin.getPasswordHash() != null && admin.getPasswordHash().contains(":")) {
            valid = PasswordUtil.verify(password, admin.getPasswordHash());
        }

        if (!valid) {
            throw new AuthException("Invalid admin credentials.");
        }

        return admin;
    }

    /**
     * Initializes default system admin if none exists in the database.
     * Documented way to initialize the first administrator.
     */
    public void initializeDefaultAdminIfEmpty() {
        try {
            if (adminDAO.countAll() == 0) {
                PasswordUtil.HashResult hr = PasswordUtil.hashPassword("admin123");
                Admin defaultAdmin = new Admin(
                    0,
                    "Campus Administrator",
                    "admin@campusconnect.edu",
                    hr.getHashBase64(),
                    hr.getSaltBase64(),
                    hr.getIterations(),
                    "SUPER"
                );
                adminDAO.insert(defaultAdmin);
                System.out.println("[AuthService] Default admin initialized: admin@campusconnect.edu / admin123");
            }
        } catch (Exception e) {
            System.err.println("[AuthService] Notice: Admin check skipped (" + e.getMessage() + ")");
        }
    }

    private void check(String errorMessage) throws AuthException {
        if (errorMessage != null) throw new AuthException(errorMessage);
    }

    public static class AuthException extends Exception {
        public AuthException(String message) { super(message); }
    }
}
