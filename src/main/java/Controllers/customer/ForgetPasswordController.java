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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email").trim();

        CustomerDAO dao = new CustomerDAO();

        Customer c = dao.findByEmailAndVerified(email);

        if (c == null) {
            request.setAttribute("error",
                    "Email không tồn tại hoặc chưa được xác thực.");
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
                    "Không thể tạo link reset. Vui lòng thử lại."
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
                "Vui lòng kiểm tra email để đặt lại mật khẩu.");
        request.getRequestDispatcher("/views/customer/forgetpassword.jsp")
                .forward(request, response);
    }
}
