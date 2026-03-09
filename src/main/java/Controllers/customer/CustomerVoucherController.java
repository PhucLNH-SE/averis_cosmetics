package Controllers.customer;

import DALs.CustomerVoucherDAO;
import Model.Customer;
import Model.CustomerVoucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class CustomerVoucherController extends HttpServlet {

    private CustomerVoucherDAO customerVoucherDAO;

    @Override
    public void init() throws ServletException {
        customerVoucherDAO = new CustomerVoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to profile with voucher tab
        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher");
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
        String code = request.getParameter("voucherCode");
        if (code == null || code.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher&error=emptyCode");
            return;
        }

        String result = customerVoucherDAO.claimVoucherWithReason(customer.getCustomerId(), code.trim());
        if ("ok".equals(result)) {
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher&success=claimed");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher&error=" + result);
    }
}
