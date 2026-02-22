package Controllers.customer;

import DALs.CartDetailDAO;
import DALs.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import Model.CartDetail;
import Model.CartItem;
import Model.Customer;
import Model.ProductVariant;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartServlet extends HttpServlet {

    ProductDAO productDAO = new ProductDAO();
    CartDetailDAO cartDetailDAO = new CartDetailDAO();

    private Map<Integer, CartItem> loadCartFromDb(int customerId) {
        Map<Integer, CartItem> cart = new HashMap<>();
        List<CartDetail> details = cartDetailDAO.getByCustomerId(customerId);
        for (CartDetail d : details) {
            ProductVariant v = productDAO.getVariantById(d.getVariantId());
            if (v != null) {
                cart.put(d.getVariantId(), new CartItem(v, d.getQuantity()));
            }
        }
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("variantId") != null) {
            doPost(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer != null && cart == null) {
            cart = loadCartFromDb(customer.getCustomerId());
            session.setAttribute("cart", cart);
        }
        if (cart == null) cart = new HashMap<>();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem i : cart.values()) {
            total = total.add(i.getSubtotal());
        }

        request.setAttribute("cart", cart);
        request.setAttribute("total", total);

        request.getRequestDispatcher("/views/customer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vRaw = request.getParameter("variantId");
        String qRaw = request.getParameter("quantity");
        
        // Mặc định action là "add" nếu không gửi lên
        String action = request.getParameter("action");
        if (action == null) action = "add"; 

        if (vRaw == null) {
            response.sendRedirect("cart");
            return;
        }

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/cart");
            String loginUrl = request.getContextPath() + "/auth?action=login";
            if ("true".equals(request.getParameter("ajax"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(loginUrl);
            } else {
                response.sendRedirect(loginUrl);
            }
            return;
        }

        try {
            int variantId = Integer.parseInt(vRaw);
            int quantity = qRaw != null ? Integer.parseInt(qRaw) : 1;

            Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
            if (cart == null) cart = loadCartFromDb(customer.getCustomerId());
            if (cart == null) cart = new HashMap<>();

            switch (action) {
                case "update":
                    if (cart.containsKey(variantId)) {
                        if (quantity <= 0) {
                            cart.remove(variantId);
                            cartDetailDAO.delete(customer.getCustomerId(), variantId);
                        } else {
                            cart.get(variantId).setQuantity(quantity);
                            cartDetailDAO.setQuantity(customer.getCustomerId(), variantId, quantity);
                        }
                    }
                    break;

                case "add":
                default:
                    ProductVariant v = productDAO.getVariantById(variantId);
                    if (v != null) {
                        if (cart.containsKey(variantId)) {
                            CartItem item = cart.get(variantId);
                            item.setQuantity(item.getQuantity() + quantity);
                        } else {
                            cart.put(variantId, new CartItem(v, quantity));
                        }
                        cartDetailDAO.addOrUpdate(customer.getCustomerId(), variantId, quantity);
                    }
                    break;
            }

            session.setAttribute("cart", cart);

            // Xử lý phản hồi (AJAX hoặc Redirect)
            String isAjax = request.getParameter("ajax");
            if ("true".equals(isAjax)) {
                response.setContentType("text/plain");
                response.getWriter().write(String.valueOf(cart.size()));
            } else {
                response.sendRedirect("cart");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("products");
        }
    }
}