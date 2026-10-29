package util;

import java.util.regex.Pattern;

/**
 * ValidationUtil — Input validation helpers used across the application.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private ValidationUtil() {}

    public static String validateName(String name) {
        if (name == null || name.isBlank()) return "Name is required.";
        if (name.trim().length() < 2)       return "Name must be at least 2 characters.";
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) return "Email is required.";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) return "Please enter a valid email address.";
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) return "Password is required.";
        if (password.length() < 6) return "Password must be at least 6 characters.";
        return null;
    }

    public static String validatePasswordMatch(String password, String confirm) {
        if (!password.equals(confirm)) return "Passwords do not match.";
        return null;
    }

    public static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) return fieldName + " is required.";
        return null;
    }

    public static String validateRating(int rating) {
        if (rating < 1 || rating > 5) return "Rating must be between 1 and 5 stars.";
        return null;
    }

    public static String validateComment(String comment, int maxLength) {
        if (comment != null && comment.length() > maxLength)
            return "Comment must not exceed " + maxLength + " characters.";
        return null;
    }
}
