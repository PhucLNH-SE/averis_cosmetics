package Controllers.customer;

import DALs.AddressDAO;
import DALs.CartDetailDAO;
import DALs.OrderDAO;
import DALs.ProductDAO;
import DALs.VoucherDAO;
import Model.Address;
import Model.CartItem;
import Model.Customer;
import Model.CustomerVoucher;
import Model.ProductVariant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PaymentController extends HttpServlet {

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

        loadCheckoutPage(req, resp, customer);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Customer customer = getLoggedInCustomer(req, resp);
        if (customer == null) {
            return;
        }

        Map<Integer, CartItem> cart = getSessionCart(req);
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
                applyVoucher(req, resp, customer, addresses, detailedCart, subtotal, voucherCode, paymentMethod, vouchers);
                break;
            default:
                placeOrder(req, resp, customer, addresses, detailedCart, subtotal, voucherCode);
        }
    }

    private void loadCheckoutPage(HttpServletRequest req, HttpServletResponse resp, Customer customer)
            throws ServletException, IOException {
        Map<Integer, CartItem> cart = getSessionCart(req);
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

    private Customer getLoggedInCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
        return customer;
    }

    private Map<Integer, CartItem> getSessionCart(HttpServletRequest req) {
        return (Map<Integer, CartItem>) req.getSession().getAttribute("cart");
    }

    private void applyVoucher(HttpServletRequest req, HttpServletResponse resp,
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

    private void placeOrder(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, List<Address> addresses,
            Map<Integer, CartItem> cart, BigDecimal subtotal,
            String voucherCode) throws IOException {

        CheckoutRequest checkoutRequest = parseCheckoutRequest(req);
        String inputError = validateCheckoutInput(checkoutRequest, addresses);
        if (inputError != null) {
            redirectError(resp, req, inputError);
            return;
        }

        VoucherSelection voucherSelection = resolveVoucherSelection(customer.getCustomerId(), voucherCode, subtotal);
        if (voucherSelection.errorMessage != null) {
            redirectError(resp, req, voucherSelection.errorMessage);
            return;
        }

        String stockValidationMessage = validateCartStock(cart);
        if (stockValidationMessage != null) {
            redirectError(resp, req, stockValidationMessage);
            return;
        }

        int orderId = createOrder(customer.getCustomerId(), checkoutRequest, cart, voucherSelection);

        if (orderId <= 0) {
            redirectError(resp, req, "Order failed! Please try again later.");
            return;
        }

        if ("COD".equals(checkoutRequest.paymentMethod)) {
            processCodPayment(req, resp, customer, orderId);
            return;
        }

        processMomoPayment(req, resp, orderId);
    }

    private CheckoutRequest parseCheckoutRequest(HttpServletRequest req) {
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.addressIdRaw = req.getParameter("addressId");

        String paymentMethod = req.getParameter("paymentMethod");
        if (paymentMethod == null) {
            paymentMethod = "";
        }
        checkoutRequest.paymentMethod = paymentMethod.trim().toUpperCase();

        try {
            checkoutRequest.addressId = Integer.parseInt(checkoutRequest.addressIdRaw);
        } catch (Exception e) {
            checkoutRequest.addressId = null;
        }

        return checkoutRequest;
    }

    private String validateCheckoutInput(CheckoutRequest checkoutRequest, List<Address> addresses) {
        if (checkoutRequest.addressIdRaw == null || checkoutRequest.addressIdRaw.isEmpty()) {
            return "Please select a delivery address!";
        }

        if (checkoutRequest.paymentMethod.isEmpty()) {
            return "Please select a payment method!";
        }

        if (checkoutRequest.addressId == null) {
            return "Invalid address!";
        }

        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == checkoutRequest.addressId);
        if (!validAddress) {
            return "Invalid address!";
        }

        if (!"COD".equals(checkoutRequest.paymentMethod) && !"MOMO".equals(checkoutRequest.paymentMethod)) {
            return "Invalid payment method!";
        }

        return null;
    }

    private VoucherSelection resolveVoucherSelection(int customerId, String voucherCode, BigDecimal subtotal) {
        VoucherSelection selection = new VoucherSelection();
        selection.discount = BigDecimal.ZERO;

        if (voucherCode == null || voucherCode.isEmpty()) {
            return selection;
        }

        VoucherResolution resolution = resolveVoucherForCheckout(customerId, voucherCode, false);
        if (!resolution.isSuccess()) {
            selection.errorMessage = "Invalid voucher!";
            return selection;
        }

        selection.voucher = resolution.getVoucher();
        selection.voucherId = selection.voucher.getVoucherId();
        selection.customerVoucherId = selection.voucher.getCustomerVoucherId();
        selection.discount = calculateDiscount(subtotal, selection.voucher);
        return selection;
    }

    private String validateCartStock(Map<Integer, CartItem> cart) {
        for (CartItem item : cart.values()) {
            ProductVariant variant = productDAO.getVariantById(item.getVariant().getVariantId());

            if (variant == null) {
                return "Product not found";
            }

            if (variant.getStock() < item.getQuantity()) {
                return "Product is out of stock: " + variant.getVariantName();
            }
        }

        return null;
    }

    private int createOrder(int customerId, CheckoutRequest checkoutRequest,
            Map<Integer, CartItem> cart, VoucherSelection voucherSelection) {
        BigDecimal finalTotal = calculateFinalTotal(calculateSubtotal(cart), voucherSelection.discount);

        return orderDAO.placeOrder(
                customerId,
                checkoutRequest.addressId,
                checkoutRequest.paymentMethod,
                finalTotal,
                new ArrayList<CartItem>(cart.values()),
                voucherSelection.voucherId,
                voucherSelection.customerVoucherId,
                voucherSelection.discount
        );
    }

    private BigDecimal calculateFinalTotal(BigDecimal subtotal, BigDecimal discount) {
        BigDecimal finalTotal = subtotal.subtract(discount == null ? BigDecimal.ZERO : discount);
        return finalTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalTotal;
    }

    private void processCodPayment(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, int orderId) throws IOException {
        HttpSession session = req.getSession();
        session.removeAttribute("cart");
        cartDetailDAO.deleteAll(customer.getCustomerId());
        resp.sendRedirect(req.getContextPath() + "/checkout?success=true&orderId=" + orderId);
    }

    private void processMomoPayment(HttpServletRequest req, HttpServletResponse resp, int orderId)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/momo-payment?orderId=" + orderId);
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

    private static final class CheckoutRequest {
        private String addressIdRaw;
        private Integer addressId;
        private String paymentMethod;
    }

    private static final class VoucherSelection {
        private CustomerVoucher voucher;
        private Integer voucherId;
        private Integer customerVoucherId;
        private BigDecimal discount;
        private String errorMessage;
    }
}

