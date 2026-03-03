/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import Model.Customer;
import Utils.MailUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author Admin
 */
public class ForgetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email").trim();

        CustomerDAO dao = new CustomerDAO();

        Customer c = dao.findByEmailAndVerified(email);

        if (c == null) {
            request.setAttribute("error",
                    "The email address does not exist or has not been verified.");
            request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                    .forward(request, response);
            return;
        }

        String token = MailUtil.generateToken();

        Timestamp expiredAt = Timestamp.valueOf(
                LocalDateTime.now().plusMinutes(15)
        );

        boolean ok = dao.saveResetPasswordToken(email, token, expiredAt);

        if (!ok) {

            request.setAttribute(
                    "error",
                    "Unable to create a reset link. Please try again."
            );
            request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                    .forward(request, response);
            return;
        }

        String link = request.getScheme() + "://"
                + request.getServerName() + ":"
                + request.getServerPort()
                + request.getContextPath()
                + "/ResetPasswordController?token=" + token;

        MailUtil.sendResetPasswordEmail(email, link);

        request.setAttribute("msg",
                "Please check your email to reset your password.");
        request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                .forward(request, response);
    }
}
