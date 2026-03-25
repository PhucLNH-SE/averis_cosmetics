package Utils;

import java.time.LocalDate;
import java.time.Period;
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
    private static final String FULL_NAME_REGEX
            = "^[A-Za-zÀ-Ỵà-ỵ]+(?: [A-Za-zÀ-Ỵà-ỵ]+)*$";

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
            String gender,
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
        } else if (!fullName.trim().matches(FULL_NAME_REGEX)) {
            errors.put("errorFullName", "Full name must contain only letters and spaces.");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.put("errorEmail", "Email is required.");
        } else if (!email.trim().matches(EMAIL_REGEX)) {
            errors.put("errorEmail", "Please enter a valid email address.");
        }

        if (gender == null || gender.trim().isEmpty()) {
            errors.put("errorGender", "Gender is required.");
        } else {
            String normalizedGender = gender.trim().toUpperCase();
            if (!"MALE".equals(normalizedGender) && !"FEMALE".equals(normalizedGender) && !"OTHER".equals(normalizedGender)) {
                errors.put("errorGender", "Please select a valid gender.");
            }
        }

        if (password == null || !password.matches(PASSWORD_REGEX)) {
            errors.put("errorPassword",
                    "Password must be at least 8 characters, include uppercase, lowercase, number and special character.");
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.put("errorConfirmPassword", "Please confirm your password.");
        } else if (password != null && !password.equals(confirmPassword)) {
            errors.put("errorConfirmPassword", "Passwords do not match.");
        }

        if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
            errors.put("errorDateOfBirth", "Date of birth is required.");
        } else {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirthStr.trim());
                LocalDate today = LocalDate.now();
                if (dob.isAfter(today)) {
                    errors.put("errorDateOfBirth", "Date of birth cannot be in the future.");
                } else if (dob.isBefore(LocalDate.of(1900, 1, 1))) {
                    errors.put("errorDateOfBirth", "Date of birth cannot be earlier than 1900.");
                } else if (java.time.Period.between(dob, today).getYears() < 13) {
                    errors.put("errorDateOfBirth", "You must be at least 13 years old.");
                }
            } catch (Exception e) {
                errors.put("errorDateOfBirth", "Please enter a valid date of birth.");
            }
        }

        return errors;
    }
    public static Map<String, String> validateEditProfile(
        String fullName,
        String gender,
        String dateOfBirthStr) {

    Map<String, String> errors = new HashMap<>();

    if (fullName == null || fullName.trim().isEmpty()) {
        errors.put("errorFullName", "Full name is required.");
    } else if (!fullName.trim().matches(FULL_NAME_REGEX)) {
        errors.put("errorFullName", "Full name must contain only letters and spaces.");
    }

    if (gender == null || gender.trim().isEmpty()) {
        errors.put("errorGender", "Gender is required.");
    } else {
        String normalizedGender = gender.trim().toUpperCase();
        if (!"MALE".equals(normalizedGender)
                && !"FEMALE".equals(normalizedGender)
                && !"OTHER".equals(normalizedGender)) {
            errors.put("errorGender", "Please select a valid gender.");
        }
    }

    if (dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty()) {
        errors.put("errorDateOfBirth", "Date of birth is required.");
    } else {
        try {
            LocalDate dob = LocalDate.parse(dateOfBirthStr.trim());
            LocalDate today = LocalDate.now();

            if (dob.isAfter(today)) {
                errors.put("errorDateOfBirth", "Date of birth cannot be in the future.");
            } else if (dob.isBefore(LocalDate.of(1900, 1, 1))) {
                errors.put("errorDateOfBirth", "Date of birth cannot be earlier than 1900.");
            } else if (Period.between(dob, today).getYears() < 13) {
                errors.put("errorDateOfBirth", "You must be at least 13 years old.");
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
public static void validateCodStatus(String paymentMethod, String paymentStatus, String orderStatus) {
    if (paymentMethod == null || paymentStatus == null || orderStatus == null) {
        throw new IllegalArgumentException("Cannot validate COD because payment method, payment status, or order status is null.");
    }

    String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
    String normalizedPaymentStatus = paymentStatus.trim().toUpperCase();
    String normalizedOrderStatus = normalizeOrderStatus(orderStatus);

    if (!"COD".equals(normalizedPaymentMethod)) {
        throw new IllegalArgumentException("This rule applies only to COD orders.");
    }

    switch (normalizedPaymentStatus) {
        case "PENDING":
            validateCodPending(normalizedOrderStatus);
            break;
        case "SUCCESS":
            validateCodSuccess(normalizedOrderStatus);
            break;
        case "FAILED":
            validateCodFailed(normalizedOrderStatus);
            break;
        default:
            throw new IllegalArgumentException("Invalid payment status for COD: " + normalizedPaymentStatus);
    }
}

public static void validateOrderStatusTransition(String currentOrderStatus, String newOrderStatus) {
    if (currentOrderStatus == null || newOrderStatus == null) {
        throw new IllegalArgumentException("Cannot update order because the current status or new status is null.");
    }

    String normalizedCurrentStatus = normalizeOrderStatus(currentOrderStatus);
    String normalizedNewStatus = normalizeOrderStatus(newOrderStatus);

    if (normalizedCurrentStatus.equals(normalizedNewStatus)) {
        return;
    }

    switch (normalizedCurrentStatus) {
        case "CREATED":
            if ("PROCESSING".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Cannot transition from CREATED to " + normalizedNewStatus +
                    ". A newly created order can only be moved to PROCESSING or CANCELLED.");
        case "PROCESSING":
            if ("SHIPPING".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Cannot transition from PROCESSING to " + normalizedNewStatus +
                    ". An order in processing can only move to SHIPPING or CANCELLED.");
        case "SHIPPING":
            if ("COMPLETED".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Cannot transition from SHIPPING to " + normalizedNewStatus +
                    ". A shipping order can only be moved to COMPLETED or CANCELLED.");
        case "COMPLETED":
            throw new IllegalArgumentException("The order is already COMPLETED. This is the final status and cannot be changed to CANCELLED or any previous status.");
        case "CANCELLED":
            throw new IllegalArgumentException("The order is CANCELLED and cannot be updated to any other status.");
        default:
            throw new IllegalArgumentException("The current order status is invalid: " + normalizedCurrentStatus);
    }
}

private static String normalizeOrderStatus(String orderStatus) {
    String normalizedOrderStatus = orderStatus.trim().toUpperCase();
    if ("CANCELED".equals(normalizedOrderStatus)) {
        return "CANCELLED";
    }
    return normalizedOrderStatus;
}

private static void validateCodPending(String orderStatus) {
    if ("CREATED".equals(orderStatus) ||
        "PROCESSING".equals(orderStatus) ||
        "SHIPPING".equals(orderStatus) ||
        "CANCELLED".equals(orderStatus)) {
        return;
    }

    if ("COMPLETED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order with a PENDING payment status cannot be in COMPLETED status, as payment has not been received.");
    }

    throw new IllegalArgumentException(
            "Invalid order status for a COD order with payment_status = PENDING: " + orderStatus);
}

private static void validateCodSuccess(String orderStatus) {
    if ("SHIPPING".equals(orderStatus) || "COMPLETED".equals(orderStatus)) {
        return;
    }

    if ("CREATED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order that has been paid cannot revert back to CREATED.");
    }

    if ("PROCESSING".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order that has been paid cannot be in PROCESSING.");
    }

    if ("CANCELLED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order that has been paid cannot be marked as CANCELLED.");
    }

    throw new IllegalArgumentException(
            "Invalid order status for a COD order with payment_status = SUCCESS: " + orderStatus);
}

private static void validateCodFailed(String orderStatus) {
    if ("CANCELLED".equals(orderStatus)) {
        return;
    }

    if ("CREATED".equals(orderStatus) ||
        "PROCESSING".equals(orderStatus) ||
        "SHIPPING".equals(orderStatus) ||
        "COMPLETED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order with payment_status = FAILED can only be in CANCELLED status.");
    }

    throw new IllegalArgumentException(
            "Invalid order status for a COD order with payment_status = FAILED: " + orderStatus);
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
