package Utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public final class StatisticUtils {

    public static final String PERIOD_MONTH = "MONTH";
    public static final String PERIOD_YEAR = "YEAR";

    private StatisticUtils() {
    }

    public static String normalizePeriodType(String raw) {
        if (raw == null) {
            return PERIOD_MONTH;
        }
        return PERIOD_YEAR.equalsIgnoreCase(raw.trim()) ? PERIOD_YEAR : PERIOD_MONTH;
    }

    public static int parseIntOrDefault(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static boolean isYearPeriod(String periodType) {
        return PERIOD_YEAR.equals(normalizePeriodType(periodType));
    }

    public static boolean isMonthPeriod(String periodType) {
        return !isYearPeriod(periodType);
    }

    public static YearMonth getDefaultPeriod() {
        return YearMonth.now();
    }

    public static boolean isValidPeriod(int year, Integer month, String periodType) {
        LocalDate now = LocalDate.now();
        if (year < 2000 || year > 2100) {
            return false;
        }

        if (isYearPeriod(periodType)) {
            return year <= now.getYear();
        }

        if (month == null || month < 1 || month > 12) {
            return false;
        }

        try {
            return !YearMonth.of(year, month).isAfter(YearMonth.now());
        } catch (Exception ex) {
            return false;
        }
    }

    public static LocalDateTime getPeriodStart(int year, Integer month, String periodType) {
        if (isYearPeriod(periodType)) {
            return LocalDate.of(year, 1, 1).atStartOfDay();
        }
        return LocalDate.of(year, month, 1).atStartOfDay();
    }

    public static LocalDateTime getPeriodEnd(int year, Integer month, String periodType) {
        LocalDate now = LocalDate.now();

        if (isYearPeriod(periodType)) {
            if (year == now.getYear()) {
                return LocalDateTime.now();
            }
            return LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        }

        if (year == now.getYear() && month == now.getMonthValue()) {
            return LocalDateTime.now();
        }

        return YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);
    }

    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }

        return BigDecimal.ZERO;
    }

    public static String toJsonLabels(List<Map<String, Object>> rows) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            array.put(String.valueOf(row.get("label")));
        }
        return array.toString();
    }

    public static String toJsonBigDecimalValues(List<Map<String, Object>> rows, String key) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            Object value = row.get(key);
            if (value instanceof BigDecimal) {
                array.put(((BigDecimal) value).doubleValue());
            } else {
                array.put(0);
            }
        }
        return array.toString();
    }

    public static String toJsonIntValues(List<Map<String, Object>> rows, String key) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> row : rows) {
            Object value = row.get(key);
            if (value instanceof Number) {
                array.put(((Number) value).intValue());
            } else {
                array.put(0);
            }
        }
        return array.toString();
    }
}
