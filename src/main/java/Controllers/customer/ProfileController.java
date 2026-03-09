/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import DALs.AddressDAO;
import DALs.OrderDAO;
import Model.Customer;
import Model.Address;
import Model.OrderDetail;
import Model.Orders;
import Utils.ValidationUtil;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lengu
 */
public class ProfileController extends HttpServlet {

    private void showProfilePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        Customer sessionCustomer = (Customer) session.getAttribute("customer");
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerById(sessionCustomer.getCustomerId());

        if (customer != null) {
            request.setAttribute("customer", customer);
            session.setAttribute("customer", customer);
        } else {
            request.setAttribute("customer", sessionCustomer);
        }

        // Handle address tab
        String tab = request.getParameter("tab");
        if ("address".equals(tab)) {
            AddressDAO addressDAO = new AddressDAO();
            List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());
            request.setAttribute("addresses", addresses);
        }

        if (session.getAttribute("profileMessage") != null) {
            request.setAttribute("profileMessage", session.getAttribute("profileMessage"));
            session.removeAttribute("profileMessage");
        }

        request.setAttribute("tab", tab);

        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void showEditForm(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer)
            throws ServletException, IOException {

        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/views/customer/editprofile.jsp")
                .forward(request, response);
    }

    private void updateProfile(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");

        String gender = request.getParameter("gender");
        String dobStr = request.getParameter("dateOfBirth");

        if (fullName == null || fullName.isBlank()) {

            request.setAttribute("error", " The full name cannot be left blank.");
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/views/customer/editprofile.jsp")
                    .forward(request, response);
            return;
        }

        LocalDate dob = null;
        if (dobStr != null && !dobStr.isBlank()) {
            try {
                dob = LocalDate.parse(dobStr.trim());
            } catch (Exception ex) {
                request.setAttribute("error", "Date of birth is not in the correct format.");
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/views/customer/editprofile.jsp")
                        .forward(request, response);
                return;
            }
        }

        customer.setFullName(fullName.trim());

        customer.setGender((gender == null || gender.isBlank()) ? null : gender.trim());
        customer.setDateOfBirth(dob);

        CustomerDAO dao = new CustomerDAO();
        boolean ok = dao.updateProfile(customer);

        if (ok) {
            request.getSession().setAttribute("customer", customer);
            response.sendRedirect(request.getContextPath() + "/profile?action=view");
        } else {
            request.setAttribute("error", "Update failed.");
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/views/customer/editprofile.jsp")
                    .forward(request, response);
        }
    }
private void showOrders(HttpServletRequest request, HttpServletResponse response,
        Customer customer) throws ServletException, IOException {

    OrderDAO dao = new OrderDAO();

    List<Orders> orders = dao.getOrdersByCustomerId(customer.getCustomerId());

    request.setAttribute("orders", orders);

    request.setAttribute("tab", "orders");

    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
}
private void showOrderDetail(HttpServletRequest request,
        HttpServletResponse response,
        Customer customer)
        throws ServletException, IOException {

    int orderId = Integer.parseInt(request.getParameter("orderId"));

    OrderDAO dao = new OrderDAO();
    List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);

    request.setAttribute("details", details);
    request.setAttribute("tab", "orderDetail");

    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
}
   private void changePassword(HttpServletRequest request,
        HttpServletResponse response,
        Customer customer) throws ServletException, IOException {

    String oldPassword = request.getParameter("oldPassword");
    String newPassword = request.getParameter("newPassword");
    String confirmPassword = request.getParameter("confirmPassword");

    // luôn set tab
    request.setAttribute("tab", "password");

    if (oldPassword == null || oldPassword.isBlank()
            || newPassword == null || newPassword.isBlank()
            || confirmPassword == null || confirmPassword.isBlank()) {

        request.setAttribute("error", "Please enter all the required information.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

   Map<String, String> errors =
        ValidationUtil.validateResetPassword(newPassword, confirmPassword);

if (!errors.isEmpty()) {
    request.setAttribute("errors", errors);
    request.setAttribute("tab", "password");
    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
    return;
}

    CustomerDAO dao = new CustomerDAO();

    String currentHash = dao.getPasswordByCustomerId(customer.getCustomerId());

    if (currentHash == null) {
        request.setAttribute("error", "Unable to retrieve the current password.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (!BCrypt.checkpw(oldPassword, currentHash)) {
        request.setAttribute("error", "The old password is incorrect.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (BCrypt.checkpw(newPassword, currentHash)) {
        request.setAttribute("error", "The new password must not be the same as the old password.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (!newPassword.equals(confirmPassword)) {
        request.setAttribute("error", "The verification password doesn't match.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    boolean ok = dao.updatePassword(
            customer.getCustomerId(),
            newPassword
    );



if (ok) {
    request.setAttribute("profileMessage", "Password changed successfully.");
    request.setAttribute("tab", "password");

    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
} else {
    request.setAttribute("error", "Password change failed.");
    request.setAttribute("tab", "password");

    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
}
}
   private void cancelOrder(HttpServletRequest request,
        HttpServletResponse response,
        Customer customer)
        throws ServletException, IOException {

    int orderId = Integer.parseInt(request.getParameter("orderId"));

    OrderDAO dao = new OrderDAO();

    boolean success = dao.cancelOrder(orderId);

    if (success) {
        response.sendRedirect(request.getContextPath() + "/profile?action=orders");
    } else {
        request.setAttribute("error", "Cannot cancel this order.");
        showOrders(request, response, customer);
    }
}

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("customer") == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=login");
        return;
    }

    Customer customer = (Customer) session.getAttribute("customer");

    String action = request.getParameter("action");
    String tab = request.getParameter("tab");

    if (action == null) {
        action = "view";
    }

    switch (action) {

        case "view":
            if ("orders".equals(tab)) {
                showOrders(request, response, customer);
            } else {
                showProfilePage(request, response);
            }
            break;

        case "edit":
            showEditForm(request, response, customer);
            break;

        case "orders":
            showOrders(request, response, customer);
            break;
case "orderDetail":
    showOrderDetail(request, response, customer);
    break;
    case "cancelOrder":
    cancelOrder(request, response, customer);
    break;
        default:
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "edit":
                updateProfile(request, response, customer);
                break;
            case "changePassword":
                changePassword(request, response, customer);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
