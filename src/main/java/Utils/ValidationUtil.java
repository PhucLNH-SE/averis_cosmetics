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

    public static void validateCodStatus(String paymentMethod, String paymentStatus, String orderStatus) {
        if (paymentMethod == null || paymentStatus == null || orderStatus == null) {
            throw new IllegalArgumentException("Khong the validate COD vi payment method, payment status hoac order status dang de trong.");
        }

        String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
        String normalizedPaymentStatus = paymentStatus.trim().toUpperCase();
        String normalizedOrderStatus = normalizeOrderStatus(orderStatus);

        if (!"COD".equals(normalizedPaymentMethod)) {
            throw new IllegalArgumentException("Rule nay chi ap dung cho don hang COD.");
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
                throw new IllegalArgumentException("Payment status khong hop le cho COD: " + normalizedPaymentStatus);
        }
    }

    public static void validateOrderStatusTransition(String currentOrderStatus, String newOrderStatus) {
        if (currentOrderStatus == null || newOrderStatus == null) {
            throw new IllegalArgumentException("Khong the cap nhat don hang vi trang thai hien tai hoac trang thai moi dang de trong.");
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
                throw new IllegalArgumentException("Khong the chuyen tu CREATED sang " + normalizedNewStatus
                        + ". Don moi tao chi duoc chuyen sang PROCESSING hoac CANCELLED.");
            case "PROCESSING":
                if ("SHIPPING".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                    return;
                }
                throw new IllegalArgumentException("Khong the chuyen tu PROCESSING sang " + normalizedNewStatus
                        + ". Don dang xu ly chi duoc chuyen sang SHIPPING hoac CANCELLED.");
            case "SHIPPING":
                if ("COMPLETED".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                    return;
                }
                throw new IllegalArgumentException("Khong the chuyen tu SHIPPING sang " + normalizedNewStatus
                        + ". Don dang giao chi duoc chuyen sang COMPLETED hoac CANCELLED.");
            case "COMPLETED":
                throw new IllegalArgumentException("Don hang da COMPLETED. Trang thai nay la cuoi cung va khong duoc chuyen sang CANCELLED hay trang thai truoc do.");
            case "CANCELLED":
                throw new IllegalArgumentException("Don hang da CANCELLED va khong the cap nhat sang trang thai khac.");
            default:
                throw new IllegalArgumentException("Order status hien tai khong hop le: " + normalizedCurrentStatus);
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
        if ("CREATED".equals(orderStatus)
                || "PROCESSING".equals(orderStatus)
                || "SHIPPING".equals(orderStatus)
                || "CANCELLED".equals(orderStatus)) {
            return;
        }

        if ("COMPLETED".equals(orderStatus)) {
            throw new IllegalArgumentException(
                    "Don COD dang PENDING khong the o trang thai COMPLETED vi chua thu tien.");
        }

        throw new IllegalArgumentException(
                "Order status khong hop le cho don COD co payment_status = PENDING: " + orderStatus);
    }

    private static void validateCodSuccess(String orderStatus) {
        if ("SHIPPING".equals(orderStatus) || "COMPLETED".equals(orderStatus)) {
            return;
        }

        if ("CREATED".equals(orderStatus)) {
            throw new IllegalArgumentException(
                    "Don COD da thanh toan khong the quay ve CREATED.");
        }

        if ("PROCESSING".equals(orderStatus)) {
            throw new IllegalArgumentException(
                    "Don COD da thanh toan khong the dung o PROCESSING.");
        }

        if ("CANCELLED".equals(orderStatus)) {
            throw new IllegalArgumentException(
                    "Don COD da thanh toan khong the chuyen sang CANCELLED.");
        }

        throw new IllegalArgumentException(
                "Order status khong hop le cho don COD co payment_status = SUCCESS: " + orderStatus);
    }

    private static void validateCodFailed(String orderStatus) {
        if ("CANCELLED".equals(orderStatus)) {
            return;
        }

        if ("CREATED".equals(orderStatus)
                || "PROCESSING".equals(orderStatus)
                || "SHIPPING".equals(orderStatus)
                || "COMPLETED".equals(orderStatus)) {
            throw new IllegalArgumentException(
                    "Don COD co payment_status = FAILED chi duoc o trang thai CANCELLED.");
        }

        throw new IllegalArgumentException(
                "Order status khong hop le cho don COD co payment_status = FAILED: " + orderStatus);
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
