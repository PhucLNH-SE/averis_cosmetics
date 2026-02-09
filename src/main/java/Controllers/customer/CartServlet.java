package Controllers.customer;

import DALs.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.CartItem;
import model.ProductVariant;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/cart", "/cart-update"})
public class CartServlet extends HttpServlet {

    ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("variantId") != null) {
            doPost(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");

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

        try {
            int variantId = Integer.parseInt(vRaw);
            int quantity = qRaw != null ? Integer.parseInt(qRaw) : 1;

            HttpSession session = request.getSession();
            Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
            if (cart == null) cart = new HashMap<>();

            // ==========================================
            // THAY ĐỔI Ở ĐÂY: DÙNG SWITCH-CASE
            // ==========================================
            switch (action) {
                case "update":
                    // Logic cập nhật hoặc xóa
                    if (cart.containsKey(variantId)) {
                        if (quantity <= 0) {
                            cart.remove(variantId); 
                        } else {
                            cart.get(variantId).setQuantity(quantity); 
                        }
                    }
                    break;

                case "add":
                default: 
                    // Logic thêm mới hoặc cộng dồn (Mặc định cũng vào đây)
                    ProductVariant v = dao.getVariantById(variantId);
                    if (v != null) {
                        if (cart.containsKey(variantId)) {
                            CartItem item = cart.get(variantId);
                            item.setQuantity(item.getQuantity() + quantity); 
                        } else {
                            cart.put(variantId, new CartItem(v, quantity)); 
                        }
                    }
                    break;
            }
            // ==========================================

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