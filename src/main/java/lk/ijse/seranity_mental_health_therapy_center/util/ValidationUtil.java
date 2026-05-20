package lk.ijse.seranity_mental_health_therapy_center.util;

/**
 * ================================================================
 *  ValidationUtil.java
 *  Seranity Mental Health Therapy Center
 * ----------------------------------------------------------------
 *  Centralized RegEx validation utility.
 *  All controllers use this class — no duplicated regex strings.
 * ================================================================
 */
public class ValidationUtil {

    // ── RegEx Patterns ────────────────────────────────────────────────────────

    /** Full name: letters + spaces + dots, min 3, max 100 chars */
    private static final String NAME_REGEX =
            "^[A-Za-z .]{3,100}$";

    /** Sri Lankan NIC: old format (9 digits + V/X) or new format (12 digits) */
    private static final String NIC_REGEX =
            "^([0-9]{9}[vVxX]|[0-9]{12})$";

    /** Sri Lanka phone: starts with +94 or 0, followed by 9 digits */
    private static final String PHONE_REGEX =
            "^(\\+94|0)[0-9]{9}$";

    /** Email: standard format — user@domain.ext */
    private static final String EMAIL_REGEX =
            "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    /** Password: minimum 6 characters */
    private static final String PASSWORD_REGEX =
            "^.{6,}$";

    /** Strong password: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char */
    private static final String STRONG_PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

    /** Username: letters, digits, dots, underscores — 3 to 30 chars */
    private static final String USERNAME_REGEX =
            "^[a-zA-Z0-9._]{3,30}$";

    /** Time: HH:mm format (24-hour) */
    private static final String TIME_REGEX =
            "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

    /** Fee / Amount: positive number, optional 2 decimal places */
    private static final String AMOUNT_REGEX =
            "^[0-9]+(\\.[0-9]{1,2})?$";

    /** Duration (weeks/months): 1 to 3 digit positive integer */
    private static final String DURATION_REGEX =
            "^[0-9]{1,3}$";

    /** Address: letters, digits, spaces, commas, dots — min 5 chars */
    private static final String ADDRESS_REGEX =
            "^[A-Za-z0-9 ,./\\-]{5,200}$";

    /** Notes / Description: any characters, min 0 — just checks not > 1000 chars */
    private static final String NOTES_REGEX =
            "^.{0,1000}$";

    // ── Private constructor — utility class, no instantiation ─────────────────

    private ValidationUtil() {}

    // ── Validation Methods ────────────────────────────────────────────────────

    /**
     * Validate full name.
     * Valid: "John Doe", "Mary Ann", "Dr. Smith"
     * Invalid: "J", "123John", ""
     */
    public static boolean isValidName(String name) {
        return name != null && name.trim().matches(NAME_REGEX);
    }

    /**
     * Validate Sri Lankan NIC.
     * Valid: "990123456V", "990123456X", "199901234560"
     * Invalid: "12345", "ABCDEFGHIJ"
     */
    public static boolean isValidNIC(String nic) {
        return nic != null && nic.trim().matches(NIC_REGEX);
    }

    /**
     * Validate Sri Lankan phone number.
     * Valid: "0771234567", "+94771234567", "0112345678"
     * Invalid: "12345", "077123456", "077-123-4567"
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.trim().matches(PHONE_REGEX);
    }

    /**
     * Validate email address.
     * Valid: "user@gmail.com", "john.doe@company.lk"
     * Invalid: "user@", "@domain.com", "plaintext"
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches(EMAIL_REGEX);
    }

    /**
     * Validate password (basic — min 6 chars).
     * Use this for login checks.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }

    /**
     * Validate strong password (min 8 chars, uppercase, lowercase, digit, special char).
     * Use this when creating / updating user accounts.
     * Valid: "Admin@123", "Secure#Pass1"
     * Invalid: "password", "12345678", "Admin123"
     */
    public static boolean isStrongPassword(String password) {
        return password != null && password.matches(STRONG_PASSWORD_REGEX);
    }

    /**
     * Validate username.
     * Valid: "admin_01", "john.doe", "User123"
     * Invalid: "ab", "user name", "user@name"
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.trim().matches(USERNAME_REGEX);
    }

    /**
     * Validate time in HH:mm format (24-hour).
     * Valid: "09:00", "14:30", "23:59"
     * Invalid: "9:00", "25:00", "09:60", "9am"
     */
    public static boolean isValidTime(String time) {
        return time != null && time.trim().matches(TIME_REGEX);
    }

    /**
     * Validate that end time is after start time.
     * Both must be in HH:mm format.
     */
    public static boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        if (!isValidTime(startTime) || !isValidTime(endTime)) return false;
        String[] start = startTime.split(":");
        String[] end   = endTime.split(":");
        int startMins  = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
        int endMins    = Integer.parseInt(end[0])   * 60 + Integer.parseInt(end[1]);
        return endMins > startMins;
    }

    /**
     * Validate fee or payment amount.
     * Valid: "80000", "50000.00", "1500.5"
     * Invalid: "-100", "abc", "50,000"
     */
    public static boolean isValidAmount(String amount) {
        return amount != null && amount.trim().matches(AMOUNT_REGEX);
    }

    /**
     * Validate duration (weeks/months) — positive integer, max 3 digits.
     * Valid: "12", "8", "100"
     * Invalid: "0", "-1", "abc", "1000"
     */
    public static boolean isValidDuration(String duration) {
        if (duration == null || !duration.trim().matches(DURATION_REGEX)) return false;
        int val = Integer.parseInt(duration.trim());
        return val > 0;
    }

    /**
     * Validate address.
     * Valid: "123 Main St, Colombo", "No. 5, Galle Rd"
     * Invalid: "Hi" (too short)
     */
    public static boolean isValidAddress(String address) {
        return address != null && address.trim().matches(ADDRESS_REGEX);
    }

    /**
     * Check if a string field is not empty.
     * Use for required fields that don't need format validation (e.g. notes, descriptions).
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if a string is within a max character limit.
     */
    public static boolean isWithinLimit(String value, int maxLength) {
        return value != null && value.length() <= maxLength;
    }

    /**
     * Check if two password strings match.
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    // ── Error Message Helpers ─────────────────────────────────────────────────
    // Controllers call these to get consistent error messages.

    public static String nameError() {
        return "⚠ Name must contain only letters (min 3, max 100 characters).";
    }

    public static String nicError() {
        return "⚠ Invalid NIC. Use old format (e.g. 990123456V) or new format (e.g. 199901234560).";
    }

    public static String phoneError() {
        return "⚠ Invalid phone number. Use format: 0771234567 or +94771234567.";
    }

    public static String emailError() {
        return "⚠ Invalid email address. Use format: user@example.com";
    }

    public static String passwordError() {
        return "⚠ Password must be at least 6 characters.";
    }

    public static String strongPasswordError() {
        return "⚠ Password must be at least 8 characters with uppercase, lowercase, digit, and special character (@#$%^&+=!).";
    }

    public static String usernameError() {
        return "⚠ Username must be 3-30 characters (letters, digits, dots, underscores only).";
    }

    public static String timeError() {
        return "⚠ Invalid time format. Use HH:mm (e.g. 09:00, 14:30).";
    }

    public static String endTimeError() {
        return "⚠ End time must be after start time.";
    }

    public static String amountError() {
        return "⚠ Invalid amount. Enter a positive number (e.g. 80000 or 1500.50).";
    }

    public static String durationError() {
        return "⚠ Duration must be a positive number (e.g. 12 for weeks).";
    }

    public static String requiredError(String fieldName) {
        return "⚠ " + fieldName + " is required.";
    }

    public static String passwordMismatchError() {
        return "⚠ Passwords do not match. Please re-enter.";
    }
}
