package Controllers.customer;

import DALs.*;
import Model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("unchecked")
public class CheckoutController extends HttpServlet {

    private AddressDAO addressDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private CartDetailDAO cartDetailDAO;
    private VoucherDAO voucherDAO;

    @Override
    public void init() {
        addressDAO = new AddressDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        cartDetailDAO = new CartDetailDAO();
        voucherDAO = new VoucherDAO();
    }

    void setVoucherDAO(VoucherDAO voucherDAO) {
        this.voucherDAO = voucherDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer customer = getLoggedInCustomer(req, resp);
        if (customer == null) {
            return;
        }

        Map<Integer, CartItem> cart = getCart(req);
        boolean isSuccess = "true".equals(req.getParameter("success"));

        if ((cart == null || cart.isEmpty()) && !isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        Map<Integer, CartItem> detailedCart = buildCartWithDetails(cart);
        BigDecimal subtotal = calculateSubtotal(detailedCart);

        setCheckoutAttributes(
                req,
                customer,
                addressDAO.getAddressesByCustomerId(customer.getCustomerId()),
                detailedCart,
                subtotal,
                BigDecimal.ZERO,
                subtotal,
                "",
                "COD",
                getCheckoutVouchers(customer.getCustomerId())
        );

        req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Customer customer = getLoggedInCustomer(req, resp);
        if (customer == null) {
            return;
        }

        Map<Integer, CartItem> cart = getCart(req);
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String action = req.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "placeOrder";
        }

        String voucherCode = req.getParameter("voucherCode");
        if (voucherCode == null) {
            voucherCode = "";
        }
        voucherCode = voucherCode.trim();

        Map<Integer, CartItem> detailedCart = buildCartWithDetails(cart);
        BigDecimal subtotal = calculateSubtotal(detailedCart);
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        List<CustomerVoucher> vouchers = getCheckoutVouchers(customer.getCustomerId());

        String paymentMethod = req.getParameter("paymentMethod");

        switch (action) {
            case "applyVoucher":
                handleApplyVoucher(req, resp, customer, addresses, detailedCart, subtotal, voucherCode, paymentMethod, vouchers);
                break;
            default:
                handlePlaceOrder(req, resp, customer, addresses, detailedCart, subtotal, voucherCode);
        }
    }

    private Customer getLoggedInCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
        return customer;
    }

    private Map<Integer, CartItem> getCart(HttpServletRequest req) {
        return (Map<Integer, CartItem>) req.getSession().getAttribute("cart");
    }

    private void handleApplyVoucher(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, List<Address> addresses,
            Map<Integer, CartItem> cart, BigDecimal subtotal,
            String voucherCode, String paymentMethod, List<CustomerVoucher> vouchers)
            throws ServletException, IOException {

        String selectedPaymentMethod = normalizePaymentMethod(paymentMethod);

        if (voucherCode.isEmpty()) {
            forwardWithError(req, resp, "Please enter a voucher code.", customer, addresses, cart, subtotal, voucherCode, selectedPaymentMethod, vouchers);
            return;
        }

        VoucherResolution resolution = resolveVoucherForCheckout(customer.getCustomerId(), voucherCode, true);
        if (!resolution.isSuccess()) {
            forwardWithError(req, resp, mapVoucherErrorMessage(resolution.getResultCode()),
                    customer, addresses, cart, subtotal, voucherCode, selectedPaymentMethod, vouchers);
            return;
        }

        CustomerVoucher voucher = resolution.getVoucher();
        BigDecimal discount = calculateDiscount(subtotal, voucher);
        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        req.setAttribute("successMessage", resolution.isClaimedDuringCheckout()
                ? "Voucher claimed and applied successfully."
                : "Voucher applied successfully.");
        setCheckoutAttributes(req, customer, addresses, cart, subtotal, discount, finalTotal, voucherCode,
                selectedPaymentMethod,
                resolution.isClaimedDuringCheckout() ? getCheckoutVouchers(customer.getCustomerId()) : vouchers);

        req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
    }

    private void handlePlaceOrder(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, List<Address> addresses,
            Map<Integer, CartItem> cart, BigDecimal subtotal,
            String voucherCode) throws IOException {

        String addressIdStr = req.getParameter("addressId");
        String paymentMethod = req.getParameter("paymentMethod");

        if (paymentMethod == null) {
            paymentMethod = "";
        }
        paymentMethod = paymentMethod.trim().toUpperCase();

        if (addressIdStr == null || addressIdStr.isEmpty()) {
            redirectError(resp, req, "Please select a delivery address!");
            return;
        }

        if (paymentMethod.isEmpty()) {
            redirectError(resp, req, "Please select a payment method!");
            return;
        }

        int addressId;
        try {
            addressId = Integer.parseInt(addressIdStr);
        } catch (Exception e) {
            redirectError(resp, req, "Invalid address!");
            return;
        }

        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == addressId);
        if (!validAddress) {
            redirectError(resp, req, "Invalid address!");
            return;
        }

        CustomerVoucher voucher = null;
        BigDecimal discount = BigDecimal.ZERO;
        Integer voucherId = null;
        Integer customerVoucherId = null;

        if (!voucherCode.isEmpty()) {
            VoucherResolution resolution = resolveVoucherForCheckout(customer.getCustomerId(), voucherCode, false);
            if (!resolution.isSuccess()) {
                redirectError(resp, req, "Invalid voucher!");
                return;
            }
            voucher = resolution.getVoucher();
            voucherId = voucher.getVoucherId();
            customerVoucherId = voucher.getCustomerVoucherId();
            discount = calculateDiscount(subtotal, voucher);
        }

        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }
        for (CartItem item : cart.values()) {
            ProductVariant variant = productDAO.getVariantById(item.getVariant().getVariantId());

            if (variant == null) {
                redirectError(resp, req, "Product not found");
                return;
            }

            if (variant.getStock() < item.getQuantity()) {
                redirectError(resp, req, "Product is out of stock: " + variant.getVariantName());
                return;
            }
        }

        int orderId = orderDAO.placeOrder(customer.getCustomerId(), addressId, paymentMethod,
                finalTotal, new ArrayList<CartItem>(cart.values()), voucherId, customerVoucherId, discount);

        if (orderId <= 0) {
            redirectError(resp, req, "Order failed! Please try again later.");
            return;
        }

        HttpSession session = req.getSession();

        switch (paymentMethod) {
            case "COD":
                session.removeAttribute("cart");
                cartDetailDAO.deleteAll(customer.getCustomerId());
                resp.sendRedirect(req.getContextPath() + "/checkout?success=true&orderId=" + orderId);
                break;

            case "MOMO":
                resp.sendRedirect(req.getContextPath() + "/momo-payment?orderId=" + orderId);
                break;

            default:
                redirectError(resp, req, "Invalid payment method!");
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error,
            Customer customer, List<Address> addresses,
            Map<Integer, CartItem> cart, BigDecimal subtotal,
            String voucherCode, String paymentMethod, List<CustomerVoucher> vouchers)
            throws ServletException, IOException {

        req.setAttribute("error", error);
        setCheckoutAttributes(req, customer, addresses, cart, subtotal, BigDecimal.ZERO, subtotal, voucherCode, paymentMethod, vouchers);
        req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
    }

    private void redirectError(HttpServletResponse resp, HttpServletRequest req, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/checkout?error=" + URLEncoder.encode(msg, "UTF-8"));
    }

    private Map<Integer, CartItem> buildCartWithDetails(Map<Integer, CartItem> cart) {
        Map<Integer, CartItem> result = new HashMap<Integer, CartItem>();
        if (cart == null) {
            return result;
        }

        for (Map.Entry<Integer, CartItem> e : cart.entrySet()) {
            ProductVariant variant = productDAO.getVariantById(e.getKey());
            if (variant != null) {
                result.put(e.getKey(), new CartItem(variant, e.getValue().getQuantity()));
            }
        }
        return result;
    }

    private BigDecimal calculateSubtotal(Map<Integer, CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, CustomerVoucher cv) {
        if (cv == null || cv.getVoucher() == null) {
            return BigDecimal.ZERO;
        }

        String type = cv.getVoucher().getDiscountType();
        BigDecimal value = cv.getVoucher().getDiscountValue();

        if (type == null || value == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        switch (type.toUpperCase()) {
            case "PERCENT":
                discount = subtotal.multiply(value)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                break;
            case "FIXED":
                discount = value;
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(subtotal) > 0) {
            return subtotal;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return discount;
    }

    VoucherResolution resolveVoucherForCheckout(int customerId, String voucherCode, boolean allowAutoClaim) {
        CustomerVoucher activeVoucher = voucherDAO.getActiveVoucherForCheckout(customerId, voucherCode);
        if (activeVoucher != null) {
            return VoucherResolution.applied(activeVoucher, false);
        }

        if (!allowAutoClaim) {
            return VoucherResolution.failed("invalidVoucher");
        }

        String claimResult = voucherDAO.claimVoucherWithReason(customerId, voucherCode);
        if ("ok".equals(claimResult) || "alreadyClaimed".equals(claimResult)) {
            CustomerVoucher claimedVoucher = voucherDAO.getActiveVoucherForCheckout(customerId, voucherCode);
            if (claimedVoucher != null) {
                return VoucherResolution.applied(claimedVoucher, "ok".equals(claimResult));
            }
        }

        return VoucherResolution.failed(claimResult);
    }

    private String mapVoucherErrorMessage(String resultCode) {
        if (resultCode == null || resultCode.trim().isEmpty()) {
            return "Invalid or expired voucher.";
        }

        switch (resultCode) {
            case "codeNotFound":
                return "Voucher code does not exist.";
            case "outOfStock":
                return "This voucher is out of stock.";
            case "voucherExpired":
                return "This voucher has expired.";
            case "alreadyClaimed":
                return "This voucher is already in your account but cannot be applied right now.";
            default:
                return "Invalid or expired voucher.";
        }
    }

    private void setCheckoutAttributes(HttpServletRequest req, Customer customer,
            List<Address> addresses, Map<Integer, CartItem> cart,
            BigDecimal subtotal, BigDecimal discount,
            BigDecimal finalTotal, String voucherCode,
            String paymentMethod,
            List<CustomerVoucher> vouchers) {

        req.setAttribute("cart", cart);
        req.setAttribute("addresses", addresses);
        req.setAttribute("total", subtotal);
        req.setAttribute("discountAmount", discount);
        req.setAttribute("finalTotal", finalTotal);
        req.setAttribute("customer", customer);
        req.setAttribute("appliedVoucherCode", voucherCode);
        req.setAttribute("selectedPaymentMethod", normalizePaymentMethod(paymentMethod));
        req.setAttribute("checkoutVouchers", vouchers);
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return "COD";
        }

        String normalized = paymentMethod.trim().toUpperCase();
        return "MOMO".equals(normalized) ? "MOMO" : "COD";
    }

    private List<CustomerVoucher> getCheckoutVouchers(int customerId) {
        voucherDAO.expireOutdatedVouchers();

        LocalDateTime now = LocalDateTime.now();
        List<CustomerVoucher> result = new ArrayList<CustomerVoucher>();

        List<CustomerVoucher> all = voucherDAO.getByCustomerId(customerId);
        for (CustomerVoucher v : all) {
            if (v == null || v.getVoucher() == null) {
                continue;
            }
            if (!"ACTIVE".equalsIgnoreCase(v.getStatus())) {
                continue;
            }

            if ((v.getEffectiveFrom() != null && now.isBefore(v.getEffectiveFrom()))
                    || (v.getEffectiveTo() != null && now.isAfter(v.getEffectiveTo()))) {
                continue;
            }
            result.add(v);
        }
        return result;
    }

    static final class VoucherResolution {

        private final CustomerVoucher voucher;
        private final String resultCode;
        private final boolean claimedDuringCheckout;

        private VoucherResolution(CustomerVoucher voucher, String resultCode, boolean claimedDuringCheckout) {
            this.voucher = voucher;
            this.resultCode = resultCode;
            this.claimedDuringCheckout = claimedDuringCheckout;
        }

        static VoucherResolution applied(CustomerVoucher voucher, boolean claimedDuringCheckout) {
            return new VoucherResolution(voucher, "ok", claimedDuringCheckout);
        }

        static VoucherResolution failed(String resultCode) {
            return new VoucherResolution(null, resultCode, false);
        }

        boolean isSuccess() {
            return voucher != null;
        }

        CustomerVoucher getVoucher() {
            return voucher;
        }

        String getResultCode() {
            return resultCode;
        }

        boolean isClaimedDuringCheckout() {
            return claimedDuringCheckout;
        }
    }
}

