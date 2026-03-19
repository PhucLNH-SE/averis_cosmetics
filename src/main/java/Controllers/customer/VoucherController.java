package Controllers.customer;

import DALs.VoucherDAO;
import Model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class VoucherController extends HttpServlet {

    private VoucherDAO voucherDAO;

    @Override
    public void init() throws ServletException {
        voucherDAO = new VoucherDAO();
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

        String result = voucherDAO.claimVoucherWithReason(customer.getCustomerId(), code.trim());
        if ("ok".equals(result)) {
            response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher&success=claimed");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher&error=" + result);
    }
}
