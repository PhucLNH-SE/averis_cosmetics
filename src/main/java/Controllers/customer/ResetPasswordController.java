/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.ValidationUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        CustomerDAO dao = new CustomerDAO();
        Customer c = dao.findByResetToken(token);

        if (c == null) {
            request.getRequestDispatcher("/views/customer/invalidtoken.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("token", token);
        request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        Map<String, String> errors
                = ValidationUtil.validateResetPassword(newPassword, confirmPassword);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                    .forward(request, response);
            return;
        }
        CustomerDAO dao = new CustomerDAO();

        boolean ok = dao.updatePasswordByToken(token, newPassword);

        if (!ok) {
            request.setAttribute("error", "The token is invalid or has expired.");
            request.getRequestDispatcher("/views/customer/resetpassword.jsp")
                    .forward(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/auth"
        );
    }
}
