package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PasswordUtil — Reusable cryptographic password utility.
 * Uses PBKDF2WithHmacSHA256 with SecureRandom salts, configurable iteration count,
 * 256-bit derived key length, and constant-time verification.
 */
public class PasswordUtil {

    public static final String ALGORITHM          = "PBKDF2WithHmacSHA256";
    public static final int    DEFAULT_ITERATIONS = 100_000;
    public static final int    KEY_LENGTH_BITS    = 256;
    public static final int    SALT_LENGTH_BYTES  = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /**
     * Value object representing the hashed password, salt, and iterations.
     */
    public static class HashResult {
        private final String hashBase64;
        private final String saltBase64;
        private final int iterations;

        public HashResult(String hashBase64, String saltBase64, int iterations) {
            this.hashBase64 = hashBase64;
            this.saltBase64 = saltBase64;
            this.iterations = iterations;
        }

        public String getHashBase64() { return hashBase64; }
        public String getSaltBase64() { return saltBase64; }
        public int getIterations()    { return iterations; }

        /** Combined string representation: salt:hash:iterations */
        public String toCombinedString() {
            return saltBase64 + ":" + hashBase64 + ":" + iterations;
        }
    }

    /**
     * Hashes a plain-text password using a newly generated salt and default iterations.
     */
    public static HashResult hashPassword(String password) {
        return hashPassword(password, DEFAULT_ITERATIONS);
    }

    /**
     * Hashes a plain-text password using a newly generated salt and specified iterations.
     */
    public static HashResult hashPassword(String password, int iterations) {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS);
        return new HashResult(
            Base64.getEncoder().encodeToString(hash),
            Base64.getEncoder().encodeToString(salt),
            iterations
        );
    }

    /**
     * Convenience method returning combined string "salt:hash:iterations".
     */
    public static String hash(String password) {
        return hashPassword(password).toCombinedString();
    }

    /**
     * Constant-time verification of password against stored Base64 hash, salt, and iterations.
     */
    public static boolean verify(String password, String storedHashBase64, String storedSaltBase64, int iterations) {
        if (password == null || storedHashBase64 == null || storedSaltBase64 == null) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(storedSaltBase64);
            byte[] expectedHash = Base64.getDecoder().decode(storedHashBase64);
            byte[] actualHash = pbkdf2(password.toCharArray(), salt, iterations > 0 ? iterations : DEFAULT_ITERATIONS, KEY_LENGTH_BITS);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies against combined string: "salt:hash:iterations" or "salt:hash".
     */
    public static boolean verify(String password, String combined) {
        if (password == null || combined == null || !combined.contains(":")) {
            return false;
        }
        String[] parts = combined.split(":");
        if (parts.length < 2) return false;
        String salt = parts[0];
        String hash = parts[1];
        int iters = (parts.length >= 3) ? Integer.parseInt(parts[2]) : DEFAULT_ITERATIONS;
        return verify(password, hash, salt, iters);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 hashing error: " + e.getMessage(), e);
        }
    }
}
