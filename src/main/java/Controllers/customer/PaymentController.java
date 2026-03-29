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


    //PhucLNH - load checkout page
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

        PaymentContext context = buildPaymentContext(customer, cart, voucherCode, req.getParameter("paymentMethod"));

        switch (action) {
            case "applyVoucher":
                applyVoucher(req, resp, context);
                break;
            default:
                processCheckout(req, resp, context);
        }
    }

    private void loadCheckoutPage(HttpServletRequest req, HttpServletResponse resp, Customer customer)
            throws ServletException, IOException {
        Map<Integer, CartItem> cart = getSessionCart(req);
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        PaymentContext context = buildPaymentContext(
                customer,
                cart,
                req.getParameter("voucherCode"),
                req.getParameter("paymentMethod")
        );
        context.errorMessage = req.getParameter("error");
        renderCheckout(req, resp, context);
    }

    //PhucLNH - check customer login
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

    private PaymentContext buildPaymentContext(Customer customer, Map<Integer, CartItem> cart,
            String voucherCode, String paymentMethod) {
        Map<Integer, CartItem> detailedCart = buildCartWithDetails(cart);
        BigDecimal subtotal = calculateSubtotal(detailedCart);

        PaymentContext context = new PaymentContext();
        context.customer = customer;
        context.cart = detailedCart;
        context.addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        context.checkoutVouchers = getCheckoutVouchers(customer.getCustomerId());
        context.subtotal = subtotal;
        context.discountAmount = BigDecimal.ZERO;
        context.finalTotal = subtotal;
        context.appliedVoucherCode = voucherCode == null ? "" : voucherCode.trim();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            context.selectedPaymentMethod = "COD";
        } else {
            String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
            context.selectedPaymentMethod = "MOMO".equals(normalizedPaymentMethod) ? "MOMO" : "COD";
        }
        return context;
    }

    private void applyVoucher(HttpServletRequest req, HttpServletResponse resp, PaymentContext context)
            throws ServletException, IOException {
        if (context.appliedVoucherCode.isEmpty()) {
            context.errorMessage = "Please enter a voucher code.";
            renderCheckout(req, resp, context);
            return;
        }

        PaymentVoucherResult voucherResult = resolveVoucher(context.customer.getCustomerId(),
                context.appliedVoucherCode, true, context.subtotal);
        if (!voucherResult.isSuccess()) {
            context.errorMessage = mapVoucherErrorMessage(voucherResult.getResultCode());
            renderCheckout(req, resp, context);
            return;
        }

        context.discountAmount = voucherResult.getDiscount();
        context.finalTotal = calculateFinalTotal(context.subtotal, voucherResult.getDiscount());
        context.successMessage = voucherResult.isClaimedDuringCheckout()
                ? "Voucher claimed and applied successfully."
                : "Voucher applied successfully.";
        if (voucherResult.isClaimedDuringCheckout()) {
            context.checkoutVouchers = getCheckoutVouchers(context.customer.getCustomerId());
        }
        renderCheckout(req, resp, context);
    }

    private void processCheckout(HttpServletRequest req, HttpServletResponse resp, PaymentContext context)
            throws IOException {
        String addressIdRaw = req.getParameter("addressId");
        Integer addressId;
        try {
            addressId = Integer.parseInt(addressIdRaw);
        } catch (Exception e) {
            addressId = null;
        }
        String paymentMethod = req.getParameter("paymentMethod");
        if (paymentMethod == null) {
            paymentMethod = "";
        }
        paymentMethod = paymentMethod.trim().toUpperCase();

        String inputError = validateCheckoutInput(addressIdRaw, addressId, paymentMethod, context.addresses);
        if (inputError != null) {
            redirectError(resp, req, inputError);
            return;
        }

        PaymentVoucherResult voucherResult = resolveVoucherForOrder(
                context.customer.getCustomerId(),
                context.appliedVoucherCode,
                context.subtotal
        );
        if (voucherResult.getErrorMessage() != null) {
            redirectError(resp, req, voucherResult.getErrorMessage());
            return;
        }

        String stockValidationMessage = validateCartStock(context.cart);
        if (stockValidationMessage != null) {
            redirectError(resp, req, stockValidationMessage);
            return;
        }

        int orderId = createOrder(
                context.customer.getCustomerId(),
                addressId,
                paymentMethod,
                context.cart,
                voucherResult
        );

        if (orderId <= 0) {
            redirectError(resp, req, "Order failed! Please try again later.");
            return;
        }

        if ("COD".equals(paymentMethod)) {
            processCodPayment(req, resp, context.customer, orderId);
            return;
        }

        processMomoPayment(req, resp, orderId);
    }

    private String validateCheckoutInput(String addressIdRaw, Integer addressId,
            String paymentMethod, List<Address> addresses) {
        if (addressIdRaw == null || addressIdRaw.isEmpty()) {
            return "Please select a delivery address!";
        }

        if (paymentMethod.isEmpty()) {
            return "Please select a payment method!";
        }

        if (addressId == null) {
            return "Invalid address!";
        }

        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == addressId);
        if (!validAddress) {
            return "Invalid address!";
        }

        if (!"COD".equals(paymentMethod) && !"MOMO".equals(paymentMethod)) {
            return "Invalid payment method!";
        }

        return null;
    }

    private PaymentVoucherResult resolveVoucherForOrder(int customerId, String voucherCode, BigDecimal subtotal) {
        if (voucherCode == null || voucherCode.isEmpty()) {
            return PaymentVoucherResult.empty();
        }

        PaymentVoucherResult voucherResult = resolveVoucher(customerId, voucherCode, false, subtotal);
        if (!voucherResult.isSuccess()) {
            voucherResult.errorMessage = "Invalid voucher!";
        }
        return voucherResult;
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

    private int createOrder(int customerId, Integer addressId, String paymentMethod,
            Map<Integer, CartItem> cart, PaymentVoucherResult voucherResult) {
        BigDecimal finalTotal = calculateFinalTotal(calculateSubtotal(cart), voucherResult.getDiscount());

        return orderDAO.placeOrder(
                customerId,
                addressId,
                paymentMethod,
                finalTotal,
                new ArrayList<CartItem>(cart.values()),
                voucherResult.getVoucherId(),
                voucherResult.getCustomerVoucherId(),
                voucherResult.getDiscount()
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
        resp.sendRedirect(req.getContextPath() + "/order-success?orderId=" + orderId);
    }

    private void processMomoPayment(HttpServletRequest req, HttpServletResponse resp, int orderId)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/momo-payment?orderId=" + orderId);
    }

    private void renderCheckout(HttpServletRequest req, HttpServletResponse resp, PaymentContext context)
            throws ServletException, IOException {
        setCheckoutAttributes(req, context);
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

    private PaymentVoucherResult resolveVoucher(int customerId, String voucherCode,
            boolean allowAutoClaim, BigDecimal subtotal) {
        CustomerVoucher activeVoucher = voucherDAO.getActiveVoucherForCheckout(customerId, voucherCode);
        if (activeVoucher != null) {
            return PaymentVoucherResult.applied(activeVoucher, false, calculateDiscount(subtotal, activeVoucher));
        }

        if (!allowAutoClaim) {
            return PaymentVoucherResult.failed("invalidVoucher");
        }

        String claimResult = voucherDAO.claimVoucherWithReason(customerId, voucherCode);
        if ("ok".equals(claimResult) || "alreadyClaimed".equals(claimResult)) {
            CustomerVoucher claimedVoucher = voucherDAO.getActiveVoucherForCheckout(customerId, voucherCode);
            if (claimedVoucher != null) {
                return PaymentVoucherResult.applied(
                        claimedVoucher,
                        "ok".equals(claimResult),
                        calculateDiscount(subtotal, claimedVoucher)
                );
            }
        }

        return PaymentVoucherResult.failed(claimResult);
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

    private void setCheckoutAttributes(HttpServletRequest req, PaymentContext context) {
        req.setAttribute("cart", context.cart);
        req.setAttribute("addresses", context.addresses);
        req.setAttribute("total", context.subtotal);
        req.setAttribute("discountAmount", context.discountAmount);
        req.setAttribute("finalTotal", context.finalTotal);
        req.setAttribute("customer", context.customer);
        req.setAttribute("appliedVoucherCode", context.appliedVoucherCode);
        req.setAttribute("selectedPaymentMethod", context.selectedPaymentMethod);
        req.setAttribute("checkoutVouchers", context.checkoutVouchers);
        req.setAttribute("error", context.errorMessage);
        req.setAttribute("successMessage", context.successMessage);
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

    private static final class PaymentContext {
        private Customer customer;
        private Map<Integer, CartItem> cart;
        private List<Address> addresses;
        private List<CustomerVoucher> checkoutVouchers;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal finalTotal;
        private String appliedVoucherCode;
        private String selectedPaymentMethod;
        private String errorMessage;
        private String successMessage;
    }

    static final class PaymentVoucherResult {
        private final CustomerVoucher voucher;
        private final String resultCode;
        private final boolean claimedDuringCheckout;
        private final BigDecimal discount;
        private String errorMessage;

        private PaymentVoucherResult(CustomerVoucher voucher, String resultCode,
                boolean claimedDuringCheckout, BigDecimal discount) {
            this.voucher = voucher;
            this.resultCode = resultCode;
            this.claimedDuringCheckout = claimedDuringCheckout;
            this.discount = discount == null ? BigDecimal.ZERO : discount;
        }

        static PaymentVoucherResult empty() {
            return new PaymentVoucherResult(null, "ok", false, BigDecimal.ZERO);
        }

        static PaymentVoucherResult applied(CustomerVoucher voucher, boolean claimedDuringCheckout,
                BigDecimal discount) {
            return new PaymentVoucherResult(voucher, "ok", claimedDuringCheckout, discount);
        }

        static PaymentVoucherResult failed(String resultCode) {
            return new PaymentVoucherResult(null, resultCode, false, BigDecimal.ZERO);
        }

        boolean isSuccess() {
            return "ok".equals(resultCode);
        }

        String getResultCode() {
            return resultCode;
        }

        boolean isClaimedDuringCheckout() {
            return claimedDuringCheckout;
        }

        BigDecimal getDiscount() {
            return discount;
        }

        Integer getVoucherId() {
            return voucher == null ? null : voucher.getVoucherId();
        }

        Integer getCustomerVoucherId() {
            return voucher == null ? null : voucher.getCustomerVoucherId();
        }

        String getErrorMessage() {
            return errorMessage;
        }
    }
}

