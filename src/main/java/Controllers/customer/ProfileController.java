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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
public class ProfileController extends HttpServlet {

    // ===== Helper: bắt buộc login =====
    private Customer requireLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/views/customer/auth/login.jsp");
            return null;
        }
        return (Customer) session.getAttribute("customer");
    }

    // ===== CASE HANDLERS =====
    private void viewProfile(HttpServletRequest request, HttpServletResponse response, Customer c)
            throws ServletException, IOException {
        request.setAttribute("c", c);
        request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Customer c)
            throws ServletException, IOException {
        request.setAttribute("c", c);
        request.getRequestDispatcher("/views/customer/editProfile.jsp").forward(request, response);
    }

  private void updateProfile(HttpServletRequest request, HttpServletResponse response, Customer c)
        throws ServletException, IOException {

    String username = request.getParameter("username");
    String fullName = request.getParameter("fullName");
    String email    = request.getParameter("email");
    String gender   = request.getParameter("gender");
    String dobStr   = request.getParameter("dateOfBirth"); // yyyy-MM-dd

    // validate tối thiểu
    if (username == null || username.isBlank() || fullName == null || fullName.isBlank()) {
        request.setAttribute("error", "Username và Full name không được để trống.");
        request.setAttribute("c", c);
        request.getRequestDispatcher("/views/customer/editProfile.jsp")
               .forward(request, response);
        return;
    }

    // parse LocalDate
    LocalDate dob = null;
    if (dobStr != null && !dobStr.isBlank()) {
        try {
            dob = LocalDate.parse(dobStr.trim());
        } catch (Exception ex) {
            request.setAttribute("error", "Date of birth không đúng định dạng.");
            request.setAttribute("c", c);
            request.getRequestDispatcher("/views/customer/editProfile.jsp")
                   .forward(request, response);
            return;
        }
    }

    // set vào object
    c.setUsername(username.trim());
    c.setFullName(fullName.trim());
    c.setEmail((email == null || email.isBlank()) ? null : email.trim());
    c.setGender((gender == null || gender.isBlank()) ? null : gender.trim());
    c.setDateOfBirth(dob);

    // gọi DAO
    CustomerDAO dao = new CustomerDAO();
    boolean ok = dao.updateProfile(c);

    if (ok) {
        request.getSession().setAttribute("customer", c);
        response.sendRedirect(request.getContextPath() + "/profile?action=view");
    } else {
        request.setAttribute("error", "Cập nhật thất bại.");
        request.setAttribute("c", c);
        request.getRequestDispatcher("/views/customer/editProfile.jsp")
               .forward(request, response);
    }
}

    // ===== ROUTING =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer c = requireLogin(request, response);
        if (c == null) return;

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) action = "view";

        switch (action) {
            case "view":
                viewProfile(request, response, c);
                break;
            case "edit":
                showEditForm(request, response, c);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer c = requireLogin(request, response);
        if (c == null) return;

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "edit":
                updateProfile(request, response, c);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
