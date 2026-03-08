package Controllers.customer;

import DALs.AddressDAO;
import DALs.OrderDAO;
import DALs.CartDetailDAO;
import Model.Address;
import Model.CartItem;
import Model.Customer;
import Model.ProductVariant;
import DALs.ProductDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
public class CheckoutController extends HttpServlet {

    private AddressDAO addressDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private CartDetailDAO cartDetailDAO;

    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        cartDetailDAO = new CartDetailDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Check if there's a success order - clear cart and show popup
        String successParam = request.getParameter("success");
        String orderIdParam = request.getParameter("orderId");

        if ("true".equals(successParam) && orderIdParam != null) {
            // Cart already cleared in doPost, just show success
            request.setAttribute("orderSuccess", true);
            request.setAttribute("orderId", orderIdParam);
        }

        // Check if there's an error
        String errorParam = request.getParameter("error");
        if (errorParam != null && !errorParam.isEmpty()) {
            request.setAttribute("error", errorParam);
        }

        // Get cart from session
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");

        // Check if cart is empty (and not a success redirect)
        if (cart == null || cart.isEmpty()) {
            if (!"true".equals(successParam)) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
        }

        // Calculate cart total and load variant info
        BigDecimal total = BigDecimal.ZERO;
        Map<Integer, CartItem> cartWithDetails = new HashMap<>();

        if (cart != null) {
            for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
                Integer variantId = entry.getKey();
                CartItem item = entry.getValue();

                // Get full variant details
                ProductVariant variant = productDAO.getVariantById(variantId);
                if (variant != null && variant.getPrice() != null) {
                    // Update item with full variant info
                    CartItem updatedItem = new CartItem(variant, item.getQuantity());
                    cartWithDetails.put(variantId, updatedItem);

                    // Calculate subtotal
                    total = total.add(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
        }

        // Get saved addresses
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());

        // Set attributes for view
        request.setAttribute("cart", cartWithDetails);
        request.setAttribute("addresses", addresses);
        request.setAttribute("total", total);
        request.setAttribute("customer", customer);

        request.getRequestDispatcher("/views/customer/checkout.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Check if logged in
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get cart from session
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Get form parameters
        String addressIdStr = request.getParameter("addressId");
        String paymentMethod = request.getParameter("paymentMethod");

        // Validate
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Vui lòng chọn địa chỉ giao hàng!", "UTF-8"));
            return;
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Vui lòng chọn phương thức thanh toán!", "UTF-8"));
            return;
        }

        int addressId;
        try {
            addressId = Integer.parseInt(addressIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Địa chỉ không hợp lệ!", "UTF-8"));
            return;
        }

        // Validate address belongs to customer
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == addressId);
        if (!validAddress) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Địa chỉ không hợp lệ!", "UTF-8"));
            return;
        }

        // Build cart items with full details
        List<CartItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            Integer variantId = entry.getKey();
            CartItem item = entry.getValue();

            ProductVariant variant = productDAO.getVariantById(variantId);
            if (variant != null && variant.getPrice() != null) {
                CartItem updatedItem = new CartItem(variant, item.getQuantity());
                items.add(updatedItem);
                total = total.add(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Giỏ hàng trống hoặc sản phẩm không tồn tại!", "UTF-8"));
            return;
        }

        // Place order with transaction
        int orderId = orderDAO.placeOrder(
                customer.getCustomerId(),
                addressId,
                paymentMethod,
                total,
                items
        );

        if (orderId > 0) {

            if ("COD".equalsIgnoreCase(paymentMethod)) {

                // COD: hoàn tất ngay
                session.removeAttribute("cart");
                cartDetailDAO.deleteAll(customer.getCustomerId());

                response.sendRedirect(
                        request.getContextPath() + "/checkout?success=true&orderId=" + orderId
                );

            } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {

                // MOMO: chuyển sang thanh toán MoMo
                response.sendRedirect(
                        request.getContextPath() + "/momo-payment?orderId=" + orderId
                );

            }

        } else {

            response.sendRedirect(request.getContextPath() + "/checkout?error="
                    + java.net.URLEncoder.encode("Đặt hàng thất bại! Vui lòng thử lại sau.", "UTF-8"));
        }
    }
}
