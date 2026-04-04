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

    private AddressDAO addressDAO = new AddressDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private CartDetailDAO cartDetailDAO = new CartDetailDAO();
    private VoucherDAO voucherDAO = new VoucherDAO();

    //PhucLNH - load checkout page
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Customer customer = getLoggedInCustomer(req, resp);
        if (customer == null) {
            return;
        }

        showCheckoutPage(req, resp, customer);
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

        String paymentMethod = req.getParameter("paymentMethod");

        switch (action) {
            case "applyVoucher":
                applyVoucher(req, resp, customer, cart, voucherCode, paymentMethod);
                break;
            default:
                placeOrder(req, resp, customer, cart, voucherCode);
        }
    }

    //PhucLNH - Payment
    private void showCheckoutPage(HttpServletRequest req, HttpServletResponse resp, Customer customer)
            throws ServletException, IOException {
        Map<Integer, CartItem> cart = getSessionCart(req);
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String appliedVoucherCode = req.getParameter("voucherCode") == null
                ? "" : req.getParameter("voucherCode").trim();
        String paymentMethod = req.getParameter("paymentMethod");
        String selectedPaymentMethod;
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            selectedPaymentMethod = "COD";
        } else {
            String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
            selectedPaymentMethod = "MOMO".equals(normalizedPaymentMethod) ? "MOMO" : "COD";
        }

        Map<Integer, CartItem> detailedCart = loadCheckoutCart(cart);
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        List<CustomerVoucher> checkoutVouchers = loadAvailableVouchers(customer.getCustomerId());
        BigDecimal subtotal = calculateSubtotal(detailedCart);
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalTotal = subtotal;

        req.setAttribute("cart", detailedCart);
        req.setAttribute("addresses", addresses);
        req.setAttribute("total", subtotal);
        req.setAttribute("discountAmount", discountAmount);
        req.setAttribute("finalTotal", finalTotal);
        req.setAttribute("customer", customer);
        req.setAttribute("appliedVoucherCode", appliedVoucherCode);
        req.setAttribute("selectedPaymentMethod", selectedPaymentMethod);
        req.setAttribute("checkoutVouchers", checkoutVouchers);
        req.setAttribute("error", req.getParameter("error"));
        req.setAttribute("successMessage", null);

        req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
    }

    //PhucLNH - check customer login
    private Customer getLoggedInCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
        return customer;
    }

    private Map<Integer, CartItem> getSessionCart(HttpServletRequest req) {
        return (Map<Integer, CartItem>) req.getSession().getAttribute("cart");
    }

    //PhucLNH - Payment
    private void applyVoucher(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, Map<Integer, CartItem> cart, String voucherCode, String paymentMethod)
            throws ServletException, IOException {
        String appliedVoucherCode = voucherCode == null ? "" : voucherCode.trim();
        String selectedPaymentMethod;
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            selectedPaymentMethod = "COD";
        } else {
            String normalizedPaymentMethod = paymentMethod.trim().toUpperCase();
            selectedPaymentMethod = "MOMO".equals(normalizedPaymentMethod) ? "MOMO" : "COD";
        }
        Map<Integer, CartItem> detailedCart = loadCheckoutCart(cart);
        BigDecimal subtotal = calculateSubtotal(detailedCart);

        if (appliedVoucherCode.isEmpty()) {
            List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
            List<CustomerVoucher> checkoutVouchers = loadAvailableVouchers(customer.getCustomerId());
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal finalTotal = subtotal;

            req.setAttribute("cart", detailedCart);
            req.setAttribute("addresses", addresses);
            req.setAttribute("total", subtotal);
            req.setAttribute("discountAmount", discountAmount);
            req.setAttribute("finalTotal", finalTotal);
            req.setAttribute("customer", customer);
            req.setAttribute("appliedVoucherCode", appliedVoucherCode);
            req.setAttribute("selectedPaymentMethod", selectedPaymentMethod);
            req.setAttribute("checkoutVouchers", checkoutVouchers);
            req.setAttribute("error", "Please enter a voucher code.");
            req.setAttribute("successMessage", null);

            req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
            return;
        }

        CustomerVoucher appliedVoucher = voucherDAO.getActiveVoucherForCheckout(customer.getCustomerId(), appliedVoucherCode);
        boolean claimedDuringCheckout = false;

        if (appliedVoucher == null) {
            String claimResult = voucherDAO.claimVoucherWithReason(customer.getCustomerId(), appliedVoucherCode);
            if ("ok".equals(claimResult) || "alreadyClaimed".equals(claimResult)) {
                appliedVoucher = voucherDAO.getActiveVoucherForCheckout(customer.getCustomerId(), appliedVoucherCode);
                claimedDuringCheckout = "ok".equals(claimResult) && appliedVoucher != null;
            }
            if (appliedVoucher == null) {
                List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
                List<CustomerVoucher> checkoutVouchers = loadAvailableVouchers(customer.getCustomerId());
                BigDecimal discountAmount = BigDecimal.ZERO;
                BigDecimal finalTotal = subtotal;

                req.setAttribute("cart", detailedCart);
                req.setAttribute("addresses", addresses);
                req.setAttribute("total", subtotal);
                req.setAttribute("discountAmount", discountAmount);
                req.setAttribute("finalTotal", finalTotal);
                req.setAttribute("customer", customer);
                req.setAttribute("appliedVoucherCode", appliedVoucherCode);
                req.setAttribute("selectedPaymentMethod", selectedPaymentMethod);
                req.setAttribute("checkoutVouchers", checkoutVouchers);
                req.setAttribute("error", mapVoucherErrorMessage(claimResult));
                req.setAttribute("successMessage", null);

                req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
                return;
            }
        }

        BigDecimal discountAmount = calculateDiscount(subtotal, appliedVoucher);
        String successMessage = claimedDuringCheckout
                ? "Voucher claimed and applied successfully."
                : "Voucher applied successfully.";
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        List<CustomerVoucher> checkoutVouchers = loadAvailableVouchers(customer.getCustomerId());
        BigDecimal finalTotal = calculateFinalTotal(subtotal, discountAmount);

        req.setAttribute("cart", detailedCart);
        req.setAttribute("addresses", addresses);
        req.setAttribute("total", subtotal);
        req.setAttribute("discountAmount", discountAmount);
        req.setAttribute("finalTotal", finalTotal);
        req.setAttribute("customer", customer);
        req.setAttribute("appliedVoucherCode", appliedVoucherCode);
        req.setAttribute("selectedPaymentMethod", selectedPaymentMethod);
        req.setAttribute("checkoutVouchers", checkoutVouchers);
        req.setAttribute("error", null);
        req.setAttribute("successMessage", successMessage);

        req.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp").forward(req, resp);
    }

    //PhucLNH - Payment
    private void placeOrder(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, Map<Integer, CartItem> cart, String voucherCode)
            throws IOException {
        Map<Integer, CartItem> detailedCart = loadCheckoutCart(cart);
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        BigDecimal subtotal = calculateSubtotal(detailedCart);
        String appliedVoucherCode = voucherCode == null ? "" : voucherCode.trim();

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

        String inputError = validateCheckoutInput(addressIdRaw, addressId, paymentMethod, addresses);
        if (inputError != null) {
            redirectError(resp, req, inputError);
            return;
        }

        CustomerVoucher appliedVoucher = null;
        if (!appliedVoucherCode.isEmpty()) {
            appliedVoucher = voucherDAO.getActiveVoucherForCheckout(customer.getCustomerId(), appliedVoucherCode);
            if (appliedVoucher == null) {
                redirectError(resp, req, "Invalid voucher!");
                return;
            }
        }

        String stockValidationMessage = validateCartStock(detailedCart);
        if (stockValidationMessage != null) {
            redirectError(resp, req, stockValidationMessage);
            return;
        }

        int orderId = createOrder(
                customer.getCustomerId(),
                addressId,
                paymentMethod,
                detailedCart,
                appliedVoucher
        );

        if (orderId <= 0) {
            redirectError(resp, req, "Order failed! Please try again later.");
            return;
        }

        if ("COD".equals(paymentMethod)) {
            processCodPayment(req, resp, customer, orderId);
            return;
        }

        processMomoPayment(req, resp, orderId);
    }

    //PhucLNH - Payment
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

    //PhucLNH - Payment
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

    //PhucLNH - Payment
    private int createOrder(int customerId, Integer addressId, String paymentMethod,
            Map<Integer, CartItem> cart, CustomerVoucher appliedVoucher) {
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal discountAmount = calculateDiscount(subtotal, appliedVoucher);
        BigDecimal finalTotal = calculateFinalTotal(subtotal, discountAmount);

        return orderDAO.placeOrder(
                customerId,
                addressId,
                paymentMethod,
                finalTotal,
                new ArrayList<CartItem>(cart.values()),
                appliedVoucher == null ? null : appliedVoucher.getVoucherId(),
                appliedVoucher == null ? null : appliedVoucher.getCustomerVoucherId(),
                discountAmount
        );
    }

    //PhucLNH - Payment
    private BigDecimal calculateFinalTotal(BigDecimal subtotal, BigDecimal discount) {
        BigDecimal finalTotal = subtotal.subtract(discount == null ? BigDecimal.ZERO : discount);
        return finalTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalTotal;
    }

    //PhucLNH - Payment
    private void processCodPayment(HttpServletRequest req, HttpServletResponse resp,
            Customer customer, int orderId) throws IOException {
        HttpSession session = req.getSession();
        session.removeAttribute("cart");
        cartDetailDAO.deleteAll(customer.getCustomerId());
        resp.sendRedirect(req.getContextPath() + "/order-success?orderId=" + orderId);
    }

    //PhucLNH - Payment
    private void processMomoPayment(HttpServletRequest req, HttpServletResponse resp, int orderId)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/momo-payment?orderId=" + orderId);
    }

    //PhucLNH - Payment
    private void redirectError(HttpServletResponse resp, HttpServletRequest req, String msg) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/checkout?error=" + URLEncoder.encode(msg, "UTF-8"));
    }

    //PhucLNH - Payment
    private Map<Integer, CartItem> loadCheckoutCart(Map<Integer, CartItem> cart) {
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

    //PhucLNH - Payment
    private BigDecimal calculateSubtotal(Map<Integer, CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    //PhucLNH - Payment
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

    //PhucLNH - Payment
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

    //PhucLNH - Payment
    private List<CustomerVoucher> loadAvailableVouchers(int customerId) {
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

}
