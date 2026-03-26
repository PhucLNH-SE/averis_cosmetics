package Controllers.customer;

import DALs.CartDetailDAO;
import DALs.ProductDAO;
import DALs.ProductVariantDAO;
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

public class CartController extends HttpServlet {

    ProductDAO productDAO = new ProductDAO();
    CartDetailDAO cartDetailDAO = new CartDetailDAO();
    ProductVariantDAO variantDAO = new ProductVariantDAO();

    private Map<Integer, CartItem> loadCartFromDb(int customerId) {
        Map<Integer, CartItem> cart = new HashMap<>();
        List<CartDetail> details = cartDetailDAO.getByCustomerId(customerId);
        for (CartDetail d : details) {
            ProductVariant v = variantDAO.getVariantById(d.getVariantId());
            if (v != null) {
                cart.put(d.getVariantId(), new CartItem(v, d.getQuantity()));
            }
        }
        return cart;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if ("getCount".equals(action)) {
            HttpSession session = request.getSession();
            Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
            int count = (cart != null) ? cart.size() : 0;
            response.getWriter().write(String.valueOf(count));
            return;
        }

        if (request.getParameter("variantId") != null) {
            doPost(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/cart");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        if (cart == null) {
            cart = loadCartFromDb(customer.getCustomerId());
            session.setAttribute("cart", cart);
        }
        if (cart == null) cart = new HashMap<>();

        BigDecimal total = BigDecimal.ZERO;
        Map<Integer, List<ProductVariant>> availableVariants = new HashMap<>();
        
        for (CartItem i : cart.values()) {
            total = total.add(i.getSubtotal());
            
            int productId = i.getVariant().getProductId();
            if (!availableVariants.containsKey(productId)) {
                availableVariants.put(productId, variantDAO.getVariantsByProductId(productId));
            }
        }

        request.setAttribute("cart", cart);
        request.setAttribute("total", total);
        request.setAttribute("availableVariants", availableVariants);

        request.getRequestDispatcher("/WEB-INF/views/customer/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vRaw = request.getParameter("variantId");
        String qRaw = request.getParameter("quantity");
        
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
                        int finalQty = cartDetailDAO.setQuantity(customer.getCustomerId(), variantId, quantity);
                        if (finalQty <= 0) {
                            cart.remove(variantId);
                        } else {
                            cart.get(variantId).setQuantity(finalQty);
                        }
                    }
                    break;

                case "changeVariant":
                    String newVariantIdRaw = request.getParameter("newVariantId");
                    if (newVariantIdRaw != null) {
                        int newVariantId = Integer.parseInt(newVariantIdRaw);
                        boolean changed = cartDetailDAO.changeVariant(customer.getCustomerId(), variantId, newVariantId);
                        if (changed) {
                            cart = loadCartFromDb(customer.getCustomerId());
                        }
                    }
                    break;

                case "add":
                default:
                    ProductVariant v = variantDAO.getVariantById(variantId);
                    if (v != null) {
                        int currentQtyInCart = cart.containsKey(variantId) ? cart.get(variantId).getQuantity() : 0;
                        
                        if (currentQtyInCart + quantity > v.getStock()) {
                            String errorMsg = "Stock limit exceeded. Only " + v.getStock() + " item(s) available for this variant.";
                            
                            if ("true".equals(request.getParameter("ajax"))) {
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                response.setContentType("text/plain;charset=UTF-8");
                                response.getWriter().write(errorMsg);
                                return;
                            } else {
                                session.setAttribute("errorMsg", errorMsg);
                                String referer = request.getHeader("referer");
                                response.sendRedirect(referer != null ? referer : "products");
                                return;
                            }
                        }

                        int finalQty = cartDetailDAO.addOrUpdate(customer.getCustomerId(), variantId, quantity);
                        if (finalQty > 0) {
                            if (cart.containsKey(variantId)) {
                                cart.get(variantId).setQuantity(finalQty);
                            } else {
                                cart.put(variantId, new CartItem(v, finalQty));
                            }
                        }
                    }
                    break;
            }

            session.setAttribute("cart", cart);

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

