package Controllers.admin;

import Model.Voucher;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VoucherControllerTest {

    private final VoucherController controller = new VoucherController();

    @Test
    void validateVoucher_acceptsPastStartDateWhenEndDateIsStillFuture() {
        Voucher voucher = buildFixedDateVoucher();
        voucher.setFixedStartAt(LocalDateTime.now().minusDays(1));
        voucher.setFixedEndAt(LocalDateTime.now().plusDays(1));

        assertDoesNotThrow(() -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsZeroQuantity() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setQuantity(0);

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsZeroDiscount() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setDiscountValue(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsPercentDiscountAboveOneHundred() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setDiscountType("PERCENT");
        voucher.setDiscountValue(new BigDecimal("101"));

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsEndDateInThePast() {
        Voucher voucher = buildFixedDateVoucher();
        voucher.setFixedStartAt(LocalDateTime.now().minusDays(1));
        voucher.setFixedEndAt(LocalDateTime.now().minusMinutes(1));

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsQuantityLowerThanClaimedQuantity() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setQuantity(1);
        voucher.setClaimedQuantity(2);

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsInvalidCodeFormat() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setCode("ab c!");

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    @Test
    void validateVoucher_rejectsActiveStatusWhenVoucherIsOutOfQuantity() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setQuantity(1);
        voucher.setClaimedQuantity(1);
        voucher.setStatus(Boolean.TRUE);

        assertThrows(IllegalArgumentException.class, () -> controller.validateVoucher(voucher));
    }

    private Voucher buildRelativeVoucher() {
        Voucher voucher = new Voucher();
        voucher.setCode("TEST");
        voucher.setDiscountType("FIXED");
        voucher.setDiscountValue(new BigDecimal("10"));
        voucher.setQuantity(1);
        voucher.setVoucherType("RELATIVE_DAYS");
        voucher.setRelativeDays(1);
        voucher.setClaimedQuantity(0);
        voucher.setStatus(Boolean.TRUE);
        return voucher;
    }

    private Voucher buildFixedDateVoucher() {
        Voucher voucher = buildRelativeVoucher();
        voucher.setVoucherType("FIXED_END_DATE");
        voucher.setRelativeDays(null);
        voucher.setFixedStartAt(LocalDateTime.now().plusDays(1));
        voucher.setFixedEndAt(LocalDateTime.now().plusDays(2));
        return voucher;
    }
}
