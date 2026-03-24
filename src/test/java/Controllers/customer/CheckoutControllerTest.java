package Controllers.customer;

import DALs.VoucherDAO;
import Model.CustomerVoucher;
import Model.Voucher;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutControllerTest {

    @Test
    void resolveVoucherForCheckout_autoClaimsVoucherWhenItWasNotClaimedYet() {
        CheckoutController controller = new CheckoutController();
        FakeVoucherDAO fakeVoucherDAO = new FakeVoucherDAO();
        fakeVoucherDAO.activeVoucherAfterClaim = createVoucher("NEWCODE", 77);
        controller.setVoucherDAO(fakeVoucherDAO);

        CheckoutController.VoucherResolution resolution = controller.resolveVoucherForCheckout(5, "NEWCODE", true);

        assertTrue(resolution.isSuccess());
        assertTrue(resolution.isClaimedDuringCheckout());
        assertNotNull(resolution.getVoucher());
        assertEquals(1, fakeVoucherDAO.claimCallCount);
    }

    @Test
    void resolveVoucherForCheckout_usesExistingVoucherWithoutClaimingAgain() {
        CheckoutController controller = new CheckoutController();
        FakeVoucherDAO fakeVoucherDAO = new FakeVoucherDAO();
        fakeVoucherDAO.activeVoucher = createVoucher("READY", 42);
        controller.setVoucherDAO(fakeVoucherDAO);

        CheckoutController.VoucherResolution resolution = controller.resolveVoucherForCheckout(5, "READY", true);

        assertTrue(resolution.isSuccess());
        assertFalse(resolution.isClaimedDuringCheckout());
        assertEquals(0, fakeVoucherDAO.claimCallCount);
        assertEquals(42, resolution.getVoucher().getCustomerVoucherId());
    }

    @Test
    void resolveVoucherForCheckout_returnsFailureWhenAutoClaimIsDisabled() {
        CheckoutController controller = new CheckoutController();
        FakeVoucherDAO fakeVoucherDAO = new FakeVoucherDAO();
        fakeVoucherDAO.claimResult = "ok";
        controller.setVoucherDAO(fakeVoucherDAO);

        CheckoutController.VoucherResolution resolution = controller.resolveVoucherForCheckout(5, "LOCKED", false);

        assertFalse(resolution.isSuccess());
        assertEquals("invalidVoucher", resolution.getResultCode());
        assertEquals(0, fakeVoucherDAO.claimCallCount);
    }

    private CustomerVoucher createVoucher(String code, int customerVoucherId) {
        Voucher voucher = new Voucher();
        voucher.setVoucherId(10);
        voucher.setCode(code);
        voucher.setDiscountType("FIXED");
        voucher.setDiscountValue(new BigDecimal("25"));

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerVoucherId(customerVoucherId);
        customerVoucher.setCustomerId(5);
        customerVoucher.setVoucherId(10);
        customerVoucher.setStatus("ACTIVE");
        customerVoucher.setVoucher(voucher);
        return customerVoucher;
    }

    private static class FakeVoucherDAO extends VoucherDAO {

        private CustomerVoucher activeVoucher;
        private CustomerVoucher activeVoucherAfterClaim;
        private String claimResult = "ok";
        private int claimCallCount;

        @Override
        public CustomerVoucher getActiveVoucherForCheckout(int customerId, String code) {
            if (activeVoucher != null) {
                return activeVoucher;
            }
            if (claimCallCount > 0) {
                return activeVoucherAfterClaim;
            }
            return null;
        }

        @Override
        public String claimVoucherWithReason(int customerId, String code) {
            claimCallCount++;
            if ("ok".equals(claimResult)) {
                activeVoucher = activeVoucherAfterClaim;
            }
            return claimResult;
        }
    }
}

