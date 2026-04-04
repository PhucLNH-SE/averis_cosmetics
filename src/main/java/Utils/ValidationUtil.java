package Utils;

import Model.ImportOrder;
import Model.ImportOrderDetail;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
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

    private static final BigInteger MAX_INTEGER_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final String IMPORT_SUPPLIER_REQUIRED_MESSAGE = "Please select a supplier.";
    private static final String IMPORT_ITEMS_REQUIRED_MESSAGE = "Please add at least one import item.";
    private static final String IMPORT_ITEM_INVALID_MESSAGE = "Please enter valid quantity and import price for each selected item.";
    private static final String IMPORT_VALID_ITEM_REQUIRED_MESSAGE = "Please add at least one valid import item.";
    private static final String IMPORT_TOTAL_LIMIT_MESSAGE = "Total amount cannot exceed 9,999,999,999 VND.";

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
    // vaidateEditProdfile
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
    // validation passowrd
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
    // bat loi theo paymentstatus
public static void validateCodStatus(
        String paymentMethod,
        String currentPaymentStatus,
        String currentOrderStatus,
        String newPaymentStatus,
        String newOrderStatus) {

    if (paymentMethod == null || currentPaymentStatus == null || currentOrderStatus == null
            || newPaymentStatus == null || newOrderStatus == null) {
        throw new IllegalArgumentException(
                "Unable to validate COD order status because one or more required values are missing.");
    }

    String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
    String normalizedCurrentPaymentStatus = currentPaymentStatus.trim().toUpperCase();
    String normalizedCurrentOrderStatus = currentOrderStatus.trim().toUpperCase();
    String normalizedNewPaymentStatus = newPaymentStatus.trim().toUpperCase();
    String normalizedNewOrderStatus = newOrderStatus.trim().toUpperCase();

    if (!"COD".equals(normalizedPaymentMethod)) {
        throw new IllegalArgumentException(
                "COD validation rules can only be applied to orders that use the COD payment method.");
    }


    if ("FAILED".equals(normalizedCurrentPaymentStatus)
            && "CANCELLED".equals(normalizedCurrentOrderStatus)
            && "PENDING".equals(normalizedNewPaymentStatus)
            && "CANCELLED".equals(normalizedNewOrderStatus)) {
        throw new IllegalArgumentException(
                "A COD order cannot be changed from FAILED + CANCELLED back to PENDING + CANCELLED.");
    }

    switch (normalizedNewPaymentStatus) {
        case "PENDING":
            validateCodPending(normalizedNewOrderStatus);
            break;
        case "SUCCESS":
            validateCodSuccess(normalizedNewOrderStatus);
            break;
        case "FAILED":
            validateCodFailed(normalizedNewOrderStatus, normalizedNewPaymentStatus);
            break;
        default:
            throw new IllegalArgumentException(
                    "Unsupported payment status for COD orders: " + normalizedNewPaymentStatus + ".");
    }
}
// bat loi theo luong xu ly don
public static void validateOrderStatusTransition(String currentOrderStatus, String newOrderStatus) {
    if (currentOrderStatus == null || newOrderStatus == null) {
        throw new IllegalArgumentException("Unable to update the order because the current status or target status is missing.");
    }

    String normalizedCurrentStatus = currentOrderStatus.trim().toUpperCase();
    String normalizedNewStatus = newOrderStatus.trim().toUpperCase();

    if (normalizedCurrentStatus.equals(normalizedNewStatus)) {
        return;
    }

    switch (normalizedCurrentStatus) {
        case "CREATED":
            if ("PROCESSING".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Invalid status transition from CREATED to " + normalizedNewStatus
                    + ". Orders in CREATED status may only be updated to PROCESSING or CANCELLED.");
        case "PROCESSING":
            if ("SHIPPING".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Invalid status transition from PROCESSING to " + normalizedNewStatus
                    + ". Orders in PROCESSING status may only be updated to SHIPPING or CANCELLED.");
        case "SHIPPING":
            if ("COMPLETED".equals(normalizedNewStatus) || "CANCELLED".equals(normalizedNewStatus)) {
                return;
            }
            throw new IllegalArgumentException("Invalid status transition from SHIPPING to " + normalizedNewStatus
                    + ". Orders in SHIPPING status may only be updated to COMPLETED or CANCELLED.");
        case "COMPLETED":
            throw new IllegalArgumentException("The order is already in COMPLETED status. This is a final status and cannot be changed.");
        case "CANCELLED":
            throw new IllegalArgumentException("The order is already CANCELLED and cannot be updated to any other status.");
        default:
            throw new IllegalArgumentException("Unsupported current order status: " + normalizedCurrentStatus + ".");
    }
}
// can go created,processing,shipping, cancelled
private static void validateCodPending(String orderStatus) {
    if ("CREATED".equals(orderStatus) ||
        "PROCESSING".equals(orderStatus) ||
        "SHIPPING".equals(orderStatus) ||
        "CANCELLED".equals(orderStatus)) {
        return;
    }

    if ("COMPLETED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order with payment status PENDING cannot be marked as COMPLETED because payment has not been received yet.");
    }

    throw new IllegalArgumentException(
            "Unsupported order status for a COD order with payment status PENDING: " + orderStatus + ".");
}
// can go shipping and completed
private static void validateCodSuccess(String orderStatus) {
    if ("SHIPPING".equals(orderStatus) || "COMPLETED".equals(orderStatus)) {
        return;
    }
if ("CREATED".equals(orderStatus)
        || "PROCESSING".equals(orderStatus)
        || "CANCELLED".equals(orderStatus)) {
    throw new IllegalArgumentException(
            "A COD order with payment status SUCCESS may only have the SHIPPING or COMPLETED order status.");
}

    throw new IllegalArgumentException(
            "Unsupported order status for a COD order with payment status SUCCESS: " + orderStatus + ".");
}
// cho failed + Cancelled di qua neu orderstatus trong cac trang thai khac thi bao loi
private static void validateCodFailed(String orderStatus, String paymentStatus) {
    if (!"FAILED".equals(paymentStatus)) {
        return;
    }

    if ("CANCELLED".equals(orderStatus)) {
        return;
    }

    if ("CREATED".equals(orderStatus)
            || "PROCESSING".equals(orderStatus)
            || "SHIPPING".equals(orderStatus)
            || "COMPLETED".equals(orderStatus)) {
        throw new IllegalArgumentException(
                "A COD order with payment status FAILED may only have the CANCELLED order status.");
    }

    throw new IllegalArgumentException(
            "Unsupported order status for a COD order with payment status FAILED: " + orderStatus + ".");
}   
public static void validateImportOrderInput(String supplierIdRaw, String[] variantIds, String[] quantities, String[] prices) {
        if (!hasText(supplierIdRaw)) {
            throw new IllegalArgumentException(IMPORT_SUPPLIER_REQUIRED_MESSAGE);
        }

        if (variantIds == null || quantities == null || prices == null) {
            throw new IllegalArgumentException(IMPORT_ITEMS_REQUIRED_MESSAGE);
        }
    }

    public static void validateImportItemInput(String variantIdRaw, String quantityRaw, String priceRaw) {
        boolean hasVariant = hasText(variantIdRaw);
        boolean hasQuantity = hasText(quantityRaw);
        boolean hasPrice = hasText(priceRaw);

        if (!hasQuantity && !hasPrice) {
            return;
        }

        if (!hasVariant || hasQuantity != hasPrice) {
            throw new IllegalArgumentException(IMPORT_ITEM_INVALID_MESSAGE);
        }
    }

    public static Integer parseImportSupplierId(String rawValue) {
        return parseIntegerValue(rawValue, IMPORT_SUPPLIER_REQUIRED_MESSAGE);
    }

    public static int parseImportVariantId(String rawValue) {
        return parseIntegerValue(rawValue, IMPORT_ITEM_INVALID_MESSAGE);
    }

    public static int parseImportQuantity(String rawValue) {
        return parseQuantityValue(rawValue, IMPORT_ITEM_INVALID_MESSAGE);
    }

    public static BigDecimal parseImportPrice(String rawValue) {
        return parseWholeNumberAmount(rawValue, IMPORT_ITEM_INVALID_MESSAGE);
    }

    public static int parseQuantityValue(String rawValue, String errorMessage) {
        if (rawValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }

        String cleaned = rawValue.replace(",", "").trim().replaceAll("\\s+", "");
        if (cleaned.isEmpty() || !cleaned.matches("\\d+")) {
            throw new IllegalArgumentException(errorMessage);
        }

        BigInteger quantityValue = new BigInteger(cleaned);
        if (quantityValue.compareTo(MAX_INTEGER_VALUE) > 0) {
            throw new IllegalArgumentException(errorMessage);
        }

        return quantityValue.intValue();
    }

    public static BigDecimal parseWholeNumberAmount(String rawValue, String errorMessage) {
        if (rawValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }

        String cleaned = rawValue.replace(",", "").trim();
        int decimalIndex = cleaned.indexOf('.');
        if (decimalIndex >= 0) {
            String fractional = cleaned.substring(decimalIndex + 1);
            if (!fractional.replace("0", "").isEmpty()) {
                throw new IllegalArgumentException(errorMessage);
            }
            cleaned = cleaned.substring(0, decimalIndex);
        }

        cleaned = cleaned.replaceAll("\\s+", "");
        if (cleaned.isEmpty() || !cleaned.matches("\\d+")) {
            throw new IllegalArgumentException(errorMessage);
        }

        return new BigDecimal(cleaned);
    }

    public static boolean isValidImportItem(ImportOrderDetail detail) {
        return detail != null
                && detail.getQuantity() > 0
                && detail.getImportPrice() != null
                && detail.getImportPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    public static void validateImportOrder(ImportOrder importOrder, BigDecimal maxTotalAmount) {
        if (importOrder == null || importOrder.getSupplierId() == null) {
            throw new IllegalArgumentException(IMPORT_SUPPLIER_REQUIRED_MESSAGE);
        }

        List<ImportOrderDetail> details = importOrder.getDetails();
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException(IMPORT_VALID_ITEM_REQUIRED_MESSAGE);
        }

        for (ImportOrderDetail detail : details) {
            if (!isValidImportItem(detail)) {
                throw new IllegalArgumentException(IMPORT_VALID_ITEM_REQUIRED_MESSAGE);
            }
        }

        BigDecimal totalAmount = importOrder.getTotalAmount() == null
                ? importOrder.calculateTotalAmount()
                : importOrder.getTotalAmount();
        if (maxTotalAmount != null && totalAmount.compareTo(maxTotalAmount) > 0) {
            throw new IllegalArgumentException(IMPORT_TOTAL_LIMIT_MESSAGE);
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static Integer parseIntegerValue(String rawValue, String errorMessage) {
        return Integer.valueOf(parseQuantityValue(rawValue, errorMessage));
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
