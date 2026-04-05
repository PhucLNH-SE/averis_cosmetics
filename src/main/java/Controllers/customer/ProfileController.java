/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import DALs.OrderDAO;
import DALs.VoucherDAO;
import DALs.FeedbackDAO;
import Model.Customer;
import Model.OrderDetail;
import Model.Orders;
import Model.CustomerVoucher;
import Utils.ValidationUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

@MultipartConfig
public class ProfileController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");

        String action = request.getParameter("action");
        String tab = request.getParameter("tab");

        if (action == null) {
            action = "view";
        }

        if ("view".equals(action) && "address".equals(tab)) {
            response.sendRedirect(request.getContextPath() + "/address");
            return;
        }

        switch (action) {

            case "view":
                if ("orders".equals(tab)) {
                    showOrders(request, response, customer);
                } else if ("feedback".equals(tab)) {
                    showFeedbacks(request, response, customer);
                } else if ("voucher".equals(tab)) {
                    showVouchers(request, response, customer);
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
            case "voucher":
                showVouchers(request, response, customer);
                break;
            case "feedback":
                showFeedbacks(request, response, customer);
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
            response.sendRedirect(request.getContextPath() + "/login");
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

    private void showProfilePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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

        String tab = request.getParameter("tab");

        consumeProfileFlashMessage(session, request);

        request.setAttribute("tab", tab);

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void showEditForm(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer)
            throws ServletException, IOException {

        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/WEB-INF/views/customer/editprofile.jsp")
                .forward(request, response);
    }

    private void updateProfile(HttpServletRequest request,
        HttpServletResponse response,
        Customer customer)
        throws ServletException, IOException {

    String fullName = request.getParameter("fullName");
    String gender = request.getParameter("gender");
    String dobStr = request.getParameter("dateOfBirth");

    Map<String, String> errors = ValidationUtil.validateEditProfile(fullName, gender, dobStr);

    if (!errors.isEmpty()) {
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        customer.setFullName(fullName);
        customer.setGender(gender);

        request.setAttribute("dateOfBirth", dobStr);
        request.setAttribute("customer", customer);

        request.getRequestDispatcher("/WEB-INF/views/customer/editprofile.jsp")
                .forward(request, response);
        return;
    }

    LocalDate dob = LocalDate.parse(dobStr.trim());

    customer.setFullName(fullName.trim());
    customer.setGender(gender.trim().toUpperCase());
    customer.setDateOfBirth(dob);

    CustomerDAO dao = new CustomerDAO();
    boolean ok = dao.updateProfile(customer);

    if (ok) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("customer", customer);
            setProfileFlashMessage(session, "Profile updated successfully.", "success");
        }
        response.sendRedirect(request.getContextPath() + "/profile?action=view");
    } else {
        request.setAttribute("error", "Update failed.");
        request.setAttribute("customer", customer);
        request.getRequestDispatcher("/WEB-INF/views/customer/editprofile.jsp")
                .forward(request, response);
    }
}

    private void showOrders(HttpServletRequest request, HttpServletResponse response,
            Customer customer) throws ServletException, IOException {

        OrderDAO dao = new OrderDAO();

        List<Orders> orders = dao.getOrdersByCustomerId(customer.getCustomerId());

        request.setAttribute("orders", orders);
        consumeProfileFlashMessage(request.getSession(false), request);

        request.setAttribute("tab", "orders");

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void showOrderDetail(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));

        OrderDAO dao = new OrderDAO();
        Orders order = dao.getOrderById(orderId);


        List<OrderDetail> details = dao.getOrderDetailsWithReview(orderId);

        request.setAttribute("order", order);
        request.setAttribute("details", details);
        consumeProfileFlashMessage(request.getSession(false), request);
        request.setAttribute("tab", "orderDetail");

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void showVouchers(HttpServletRequest request, HttpServletResponse response,
            Customer customer) throws ServletException, IOException {

        VoucherDAO dao = new VoucherDAO();
        dao.expireOutdatedVouchers();
        List<CustomerVoucher> vouchers = dao.getActiveByCustomerId(customer.getCustomerId());

        request.setAttribute("myVouchers", vouchers);
        consumeProfileFlashMessage(request.getSession(false), request);
        request.setAttribute("tab", "voucher");

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void showFeedbacks(HttpServletRequest request, HttpServletResponse response,
            Customer customer) throws ServletException, IOException {

        FeedbackDAO dao = new FeedbackDAO();
        List<OrderDetail> feedbacks = dao.getFeedbacksByCustomerId(customer.getCustomerId());

        request.setAttribute("myFeedbacks", feedbacks);
        consumeProfileFlashMessage(request.getSession(false), request);
        request.setAttribute("tab", "feedback");

        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                .forward(request, response);
    }

    private void consumeProfileFlashMessage(HttpSession session, HttpServletRequest request) {
        if (session != null && session.getAttribute("profileMessage") != null) {
            request.setAttribute("profileMessage", session.getAttribute("profileMessage"));
            if (session.getAttribute("profileMessageType") != null) {
                request.setAttribute("profileMessageType", session.getAttribute("profileMessageType"));
                session.removeAttribute("profileMessageType");
            }
            session.removeAttribute("profileMessage");
        }
    }

    private void setProfileFlashMessage(HttpSession session, String message, String type) {
        if (session == null) {
            return;
        }

        session.setAttribute("profileMessage", message);
        session.setAttribute("profileMessageType", type);
    }

    private void changePassword(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer) throws ServletException, IOException {

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
      CustomerDAO dao = new CustomerDAO();
     

        String currentHash = dao.getPasswordByCustomerId(customer.getCustomerId());
        request.setAttribute("tab", "password");

        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {

            request.setAttribute("error", "Please enter all the required information.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
            return;
        }

        Map<String, String> errors
                = ValidationUtil.validateResetPassword(newPassword, confirmPassword);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("tab", "password");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
            return;
        }

  

        if (currentHash == null) {
            request.setAttribute("error", "Unable to retrieve the current password.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
            return;
        }

        if (!BCrypt.checkpw(oldPassword, currentHash)) {
            request.setAttribute("error", "The old password is incorrect.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
            return;
        }

        if (BCrypt.checkpw(newPassword, currentHash)) {
            request.setAttribute("error", "The new password must not be the same as the old password.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "The verification password doesn't match.");
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
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

            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
        } else {
            request.setAttribute("error", "Password change failed.");
            request.setAttribute("tab", "password");

            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp")
                    .forward(request, response);
        }
    }

    private void cancelOrder(HttpServletRequest request,
            HttpServletResponse response,
            Customer customer)
            throws ServletException, IOException {

        int orderId = Integer.parseInt(request.getParameter("orderId"));
        HttpSession session = request.getSession(false);

        OrderDAO dao = new OrderDAO();

        boolean success = dao.cancelOrder(orderId);

        if (success) {
            setProfileFlashMessage(session,
                    "Order #" + orderId + " has been cancelled successfully.",
                    "success");
            response.sendRedirect(request.getContextPath() + "/profile?action=orders");
        } else {
            setProfileFlashMessage(session,
                    "Unable to cancel this order. Please try again.",
                    "error");
            response.sendRedirect(request.getContextPath() + "/profile?action=orders");
        }
    }
}

