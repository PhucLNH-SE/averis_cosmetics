package Controllers.customer;

import DALs.AddressDAO;
import DALs.OrderDAO;
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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
@SuppressWarnings("unchecked")
public class CheckoutController extends HttpServlet {

    private AddressDAO addressDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        addressDAO = new AddressDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Get cart from session
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");

        // Check if cart is empty
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Calculate cart total and load variant info
        BigDecimal total = BigDecimal.ZERO;
        Map<Integer, CartItem> cartWithDetails = new HashMap<>();

        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            Integer variantId = entry.getKey();
            CartItem item = entry.getValue();

            // Get full variant details
            ProductVariant variant = productDAO.getVariantById(variantId);
            if (variant != null) {
                // Update item with full variant info
                CartItem updatedItem = new CartItem(variant, item.getQuantity());
                cartWithDetails.put(variantId, updatedItem);

                // Calculate subtotal
                if (variant.getPrice() != null) {
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

        System.out.println("=== CHECKOUT POST START ===");
        
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Check if logged in
        if (customer == null) {
            System.out.println("Customer is null - redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get cart from session
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            System.out.println("Cart is empty - redirecting to cart");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Get form parameters
        String addressIdStr = request.getParameter("addressId");
        String paymentMethod = request.getParameter("paymentMethod");
        
        System.out.println("addressId: " + addressIdStr);
        System.out.println("paymentMethod: " + paymentMethod);

        // Validate
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng chọn địa chỉ giao hàng!");
            doGet(request, response);
            return;
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng chọn phương thức thanh toán!");
            doGet(request, response);
            return;
        }

        int addressId;
        try {
            addressId = Integer.parseInt(addressIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Địa chỉ không hợp lệ!");
            doGet(request, response);
            return;
        }

        // Validate address belongs to customer
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
        boolean validAddress = addresses.stream().anyMatch(a -> a.getAddressId() == addressId);
        if (!validAddress) {
            request.setAttribute("error", "Địa chỉ không hợp lệ!");
            doGet(request, response);
            return;
        }

        // Build cart items with full details
        List<CartItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, CartItem> entry : cart.entrySet()) {
            Integer variantId = entry.getKey();
            CartItem item = entry.getValue();

            ProductVariant variant = productDAO.getVariantById(variantId);
            if (variant != null) {
                CartItem updatedItem = new CartItem(variant, item.getQuantity());
                items.add(updatedItem);
                total = total.add(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        if (items.isEmpty()) {
            request.setAttribute("error", "Giỏ hàng trống hoặc sản phẩm không tồn tại!");
            doGet(request, response);
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
            // Order success - clear cart and redirect to success page
            session.removeAttribute("cart");
            response.sendRedirect(request.getContextPath() + "/order-success?orderId=" + orderId);
        } else {
            // Order failed
            request.setAttribute("error", "Đặt hàng thất bại! Vui lòng thử lại sau.");
            doGet(request, response);
        }
    }
}
