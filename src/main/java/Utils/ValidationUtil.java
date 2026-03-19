package Utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

public class ValidationUtil {

    public ValidationUtil() {
    }

    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";

    private static final String PASSWORD_REGEX
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final String EMAIL_REGEX
            = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static Map<String, String> validateLogin(String username, String password) {

        Map<String, String> errors = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            errors.put("errorUsername", "Username is required.");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("errorPassword", "Password is required.");
        }

        return errors;
    }

    public static Map<String, String> validateRegistration(String username,
            String fullName,
            String email,
            String password,
            String confirmPassword,
            String dateOfBirthStr) {

        Map<String, String> errors = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            errors.put("errorUsername", "Username is required.");
        } else if (!username.trim().matches(USERNAME_REGEX)) {
            errors.put("errorUsername",
                    "Username must be 3-20 characters and contain only letters, numbers, or underscore.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("errorFullName", "Full name is required.");
        } else if (fullName.trim().length() < 2) {
            errors.put("errorFullName", "Full name must be at least 2 characters long.");
        }

        if (email != null && !email.trim().isEmpty() && !email.trim().matches(EMAIL_REGEX)) {
            errors.put("errorEmail", "Please enter a valid email address.");
        }

        if (password == null || !password.matches(PASSWORD_REGEX)) {
            errors.put("errorPassword",
                    "Password must be at least 8 characters, include uppercase, lowercase, number and special character.");
        }

        if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
            errors.put("errorConfirmPassword", "Passwords do not match.");
        }

        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            errors.put("errorDateOfBirth", "Date of birth is required.");
        } else {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirthStr.trim());
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("errorDateOfBirth", "Date of birth cannot be in the future.");
                }
            } catch (Exception e) {
                errors.put("errorDateOfBirth", "Please enter a valid date of birth.");
            }
        }

        return errors;
    }
    public static Map<String, String> validateResetPassword(
        String password,
        String confirmPassword
) {

    Map<String, String> errors = new HashMap<>();

    if (password == null || password.trim().isEmpty()) {
        errors.put("errorPassword", "Password is required.");
    } else if (!password.matches(PASSWORD_REGEX)) {
        errors.put("errorPassword",
                "Password must be at least 8 characters, include uppercase, lowercase, number and special character.");
    }

  
   return errors;
}
     public boolean checkLogin(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }

        if (inputPassword.equals(storedPassword)) {
            return true;
        }

        try {
            return BCrypt.checkpw(inputPassword, storedPassword);
        } catch (Exception ex) {
            return false;
        }
    }
    
}
