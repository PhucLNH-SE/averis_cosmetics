package Controllers.customer;

import DALs.AddressDAO;
import DALs.CartDetailDAO;
import DALs.CustomerVoucherDAO;
import DALs.OrderDAO;
import DALs.ProductDAO;
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
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CheckoutController extends HttpServlet {

    private AddressDAO addressDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private CartDetailDAO cartDetailDAO;
    private CustomerVoucherDAO customerVoucherDAO;

    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        cartDetailDAO = new CartDetailDAO();
        customerVoucherDAO = new CustomerVoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String successParam = request.getParameter("success");
        String orderIdParam = request.getParameter("orderId");

        if ("true".equals(successParam) && orderIdParam != null) {
            request.setAttribute("orderSuccess", true);
            request.setAttribute("orderId", orderIdParam);
        }

        String errorParam = request.getParameter("error");
        if (errorParam != null && !errorParam.isEmpty()) {
            request.setAttribute("error", errorParam);
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if ((cart == null || cart.isEmpty()) && !"true".equals(successParam)) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Map<Integer, CartItem> cartWithDetails = buildCartWithDetails(cart);
        BigDecimal total = calculateSubtotal(cartWithDetails);
        List<CustomerVoucher> checkoutVouchers = getCheckoutVouchers(customer.getCustomerId());

        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        request.setAttribute("cart", cartWithDetails);
        request.setAttribute("addresses", addresses);
        request.setAttribute("total", total);
        request.setAttribute("discountAmount", BigDecimal.ZERO);
        request.setAttribute("finalTotal", total);
        request.setAttribute("customer", customer);
        request.setAttribute("checkoutVouchers", checkoutVouchers);

        request.getRequestDispatcher("/views/customer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "placeOrder";
        }

        Map<Integer, CartItem> cartWithDetails = buildCartWithDetails(cart);
        BigDecimal subtotal = calculateSubtotal(cartWithDetails);
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        List<CustomerVoucher> checkoutVouchers = getCheckoutVouchers(customer.getCustomerId());

        String voucherCode = request.getParameter("voucherCode");
        if (voucherCode != null) {
            voucherCode = voucherCode.trim();
        }

        switch (action) {
            case "applyVoucher":
                applyVoucherPreview(request, response, customer, addresses, cartWithDetails, subtotal, voucherCode, checkoutVouchers);
                return;
            case "placeOrder":
            default:
                placeOrderWithVoucher(request, response, customer, addresses, cartWithDetails, subtotal, voucherCode);
                return;
        }
    }

    private void applyVoucherPreview(HttpServletRequest request, HttpServletResponse response,
                                     Customer customer, List<Address> addresses,
                                     Map<Integer, CartItem> cart, BigDecimal subtotal,
                                     String voucherCode, List<CustomerVoucher> checkoutVouchers)
            throws ServletException, IOException {
        if (voucherCode == null || voucherCode.isEmpty()) {
            request.setAttribute("error", "Vui long nhap ma voucher.");
            setCheckoutAttributes(request, customer, addresses, cart, subtotal, BigDecimal.ZERO, subtotal, "", checkoutVouchers);
            request.getRequestDispatcher("/views/customer/checkout.jsp").forward(request, response);
            return;
        }

        CustomerVoucher voucher = customerVoucherDAO.getActiveVoucherForCheckout(customer.getCustomerId(), voucherCode);
        if (voucher == null) {
            request.setAttribute("error", "Voucher khong hop le hoac da het han.");
            setCheckoutAttributes(request, customer, addresses, cart, subtotal, BigDecimal.ZERO, subtotal, voucherCode, checkoutVouchers);
            request.getRequestDispatcher("/views/customer/checkout.jsp").forward(request, response);
            return;
        }

        BigDecimal discount = calculateDiscount(subtotal, voucher);
        BigDecimal finalTotal = subtotal.subtract(discount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        request.setAttribute("successMessage", "Da ap dung voucher thanh cong.");
        setCheckoutAttributes(request, customer, addresses, cart, subtotal, discount, finalTotal, voucherCode, checkoutVouchers);
        request.getRequestDispatcher("/views/customer/checkout.jsp").forward(request, response);
    }

    private void placeOrderWithVoucher(HttpServletRequest request, HttpServletResponse response,
                                       Customer customer, List<Address> addresses,
                                       Map<Integer, CartItem> cart, BigDecimal subtotal,
                                       String voucherCode) throws IOException {
        String addressIdStr = request.getParameter("addressId");
        String paymentMethod = request.getParameter("paymentMethod");

        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
        + URLEncoder.encode("Vui lòng chọn địa chỉ giao hàng!", "UTF-8"));
            return;
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error=" + URLEncoder.encode("Vui long chon phuong thuc thanh toan!", "UTF-8"));
            return;
        }

        int addressId;
        try {
            addressId = Integer.parseInt(addressIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/checkout?error=" + URLEncoder.encode("Dia chi khong hop le!", "UTF-8"));
            return;
        }

        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == addressId);
        if (!validAddress) {
            response.sendRedirect(request.getContextPath() + "/checkout?error=" + URLEncoder.encode("Dia chi khong hop le!", "UTF-8"));
            return;
        }

        List<CartItem> items = new ArrayList<>(cart.values());
        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error=" + URLEncoder.encode("Gio hang trong!", "UTF-8"));
            return;
        }

        Integer voucherId = null;
        Integer customerVoucherId = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucherCode != null && !voucherCode.isEmpty()) {
            CustomerVoucher voucher = customerVoucherDAO.getActiveVoucherForCheckout(customer.getCustomerId(), voucherCode);
            if (voucher == null) {
                response.sendRedirect(request.getContextPath() + "/checkout?error=" + URLEncoder.encode("Voucher khong hop le hoac da het han.", "UTF-8"));
                return;
            }
            voucherId = voucher.getVoucherId();
            customerVoucherId = voucher.getCustomerVoucherId();
            discountAmount = calculateDiscount(subtotal, voucher);
        }

        BigDecimal finalTotal = subtotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        int orderId = orderDAO.placeOrder(customer.getCustomerId(), addressId, paymentMethod, finalTotal, items, voucherId, discountAmount);

if (orderId > 0) {

    if (customerVoucherId != null) {
        customerVoucherDAO.markVoucherUsed(customerVoucherId);
    }

    HttpSession session = request.getSession();

    if ("COD".equalsIgnoreCase(paymentMethod)) {

        session.removeAttribute("cart");
        cartDetailDAO.deleteAll(customer.getCustomerId());

        response.sendRedirect(
                request.getContextPath() + "/checkout?success=true&orderId=" + orderId
        );

    } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {

        response.sendRedirect(
                request.getContextPath() + "/momo-payment?orderId=" + orderId
        );
    }

    return;
}

response.sendRedirect(
        request.getContextPath() + "/checkout?error=" +
        URLEncoder.encode("Dat hang that bai! Vui long thu lai sau.", "UTF-8")
);
    }

    private Map<Integer, CartItem> buildCartWithDetails(Map<Integer, CartItem> cart) {
        Map<Integer, CartItem> result = new HashMap<>();
        if (cart == null) {
            return result;
        }

        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            ProductVariant variant = productDAO.getVariantById(entry.getKey());
            if (variant != null && variant.getPrice() != null) {
                result.put(entry.getKey(), new CartItem(variant, entry.getValue().getQuantity()));
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

    private BigDecimal calculateDiscount(BigDecimal subtotal, CustomerVoucher customerVoucher) {
        if (customerVoucher == null || customerVoucher.getVoucher() == null || subtotal == null) {
            return BigDecimal.ZERO;
        }

        String discountType = customerVoucher.getVoucher().getDiscountType();
        BigDecimal value = customerVoucher.getVoucher().getDiscountValue();
        if (discountType == null || value == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        switch (discountType.toUpperCase()) {
            case "PERCENT":
                discount = subtotal.multiply(value).divide(new BigDecimal("100"));
                break;
            case "FIXED":
                discount = value;
                break;
            default:
                discount = BigDecimal.ZERO;
                break;
        }

        if (discount.compareTo(subtotal) > 0) {
            return subtotal;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return discount;
    }

    private void setCheckoutAttributes(HttpServletRequest request, Customer customer, List<Address> addresses,
                                       Map<Integer, CartItem> cart, BigDecimal subtotal,
                                       BigDecimal discountAmount, BigDecimal finalTotal,
                                       String voucherCode, List<CustomerVoucher> checkoutVouchers) {
        request.setAttribute("cart", cart);
        request.setAttribute("addresses", addresses);
        request.setAttribute("total", subtotal);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("finalTotal", finalTotal);
        request.setAttribute("customer", customer);
        request.setAttribute("appliedVoucherCode", voucherCode);
        request.setAttribute("checkoutVouchers", checkoutVouchers);
    }

    private List<CustomerVoucher> getCheckoutVouchers(int customerId) {
        customerVoucherDAO.expireOutdatedVouchers();
        List<CustomerVoucher> allVouchers = customerVoucherDAO.getByCustomerId(customerId);
        List<CustomerVoucher> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (CustomerVoucher voucher : allVouchers) {
            if (voucher == null || voucher.getVoucher() == null || voucher.getStatus() == null) {
                continue;
            }

            if (!"ACTIVE".equalsIgnoreCase(voucher.getStatus())) {
                continue;
            }

            LocalDateTime effectiveFrom = voucher.getEffectiveFrom();
            LocalDateTime effectiveTo = voucher.getEffectiveTo();

            if (effectiveFrom != null && now.isBefore(effectiveFrom)) {
                continue;
            }
            if (effectiveTo != null && now.isAfter(effectiveTo)) {
                continue;
            }

            result.add(voucher);
        }
        return result;
    }
}
