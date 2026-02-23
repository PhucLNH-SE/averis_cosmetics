/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

/**
 *
 * @author lengu
 */
public class CustomerController extends HttpServlet {

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
        if (session.getAttribute("profileMessage") != null) {
            request.setAttribute("profileMessage", session.getAttribute("profileMessage"));
            session.removeAttribute("profileMessage");
        }
        request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
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
        String email = request.getParameter("email");
        String gender = request.getParameter("gender");
        String dobStr = request.getParameter("dateOfBirth");

        if (fullName == null || fullName.isBlank()) {

            request.setAttribute("error", " Full name không được để trống.");
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
                request.setAttribute("error", "Date of birth không đúng định dạng.");
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/views/customer/editprofile.jsp")
                        .forward(request, response);
                return;
            }
        }

       
        customer.setFullName(fullName.trim());
        customer.setEmail((email == null || email.isBlank()) ? null : email.trim());
        customer.setGender((gender == null || gender.isBlank()) ? null : gender.trim());
        customer.setDateOfBirth(dob);

        CustomerDAO dao = new CustomerDAO();
        boolean ok = dao.updateProfile(customer);

        if (ok) {
            request.getSession().setAttribute("customer", customer);
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view");
        } else {
            request.setAttribute("error", "Cập nhật thất bại.");
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/views/customer/editPprofile.jsp")
                    .forward(request, response);
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
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "view":
                showProfilePage(request, response);
                break;
            case "edit":
                showEditForm(request, response, customer);
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

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
