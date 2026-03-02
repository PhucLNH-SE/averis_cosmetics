/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.CustomerDAO;
import DALs.AddressDAO;
import Model.Customer;
import Model.Address;
import java.util.List;
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
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lengu
 */
@MultipartConfig
public class ProfileController extends HttpServlet {
 private void changeAvatar(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {

    Part part = request.getPart("avatar"); // name="avatar"


    if (part == null || part.getSize() == 0) {
        response.sendRedirect(request.getContextPath() + "/profile?action=view");
        return;
    }

    // Lấy tên file gốc
    String fileName = Paths.get(part.getSubmittedFileName())
                           .getFileName()
                           .toString();

    // Thư mục lưu avatar
    String uploadDir = getServletContext().getRealPath("/assets/avatar");

    File dir = new File(uploadDir);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    // Tạo tên file mới tránh trùng
    String newFileName = System.currentTimeMillis() + "_" + fileName;

    String fullPath = uploadDir + File.separator + newFileName;

    // Lưu file
    part.write(fullPath);

    // Update DB
    Customer c = (Customer) request.getSession().getAttribute("customer");

    try {
        CustomerDAO customerDAO = new CustomerDAO();
        customerDAO.updateAvatar(c.getCustomerId(), newFileName);

        // cập nhật lại session
        c.setAvatar(newFileName);
        request.getSession().setAttribute("customer", c);

    } catch (SQLException e) {
        throw new ServletException(e);
    }

    response.sendRedirect(request.getContextPath() + "/profile?action=view");
}
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

        request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (newPassword.length() < 6) {
        request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    CustomerDAO dao = new CustomerDAO();

    String currentHash = dao.getPasswordByCustomerId(customer.getCustomerId());

    if (currentHash == null) {
        request.setAttribute("error", "Không lấy được mật khẩu hiện tại.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (!BCrypt.checkpw(oldPassword, currentHash)) {
        request.setAttribute("error", "Mật khẩu cũ không đúng.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (BCrypt.checkpw(newPassword, currentHash)) {
        request.setAttribute("error", "Mật khẩu mới không được trùng mật khẩu cũ.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    if (!newPassword.equals(confirmPassword)) {
        request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
        request.getRequestDispatcher("/views/customer/profile.jsp")
                .forward(request, response);
        return;
    }

    boolean ok = dao.updatePassword(
            customer.getCustomerId(),
            newPassword
    );



if (ok) {
    request.setAttribute("profileMessage", "Đổi mật khẩu thành công.");
    request.setAttribute("tab", "password");

    request.getRequestDispatcher("/views/customer/profile.jsp")
            .forward(request, response);
} else {
    request.setAttribute("error", "Đổi mật khẩu thất bại.");
    request.setAttribute("tab", "password");

    request.getRequestDispatcher("/views/customer/profile.jsp")
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
            case "changePassword":
                changePassword(request, response, customer);
                break;
                case "changeAvatar":
    changeAvatar(request, response);
    break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
