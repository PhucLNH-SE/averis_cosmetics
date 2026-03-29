package Controllers.customer;

import DALs.VoucherDAO;
import Model.Customer;
import Model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class VoucherController extends HttpServlet {

    private VoucherDAO voucherDAO;

    @Override
    public void init() throws ServletException {
        voucherDAO = new VoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isFreeVoucherRoute(request)) {
            showFreeVoucherPage(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String code = request.getParameter("voucherCode");
        if (code == null || code.trim().isEmpty()) {
            response.sendRedirect(buildVoucherRedirect(request, "error", "emptyCode"));
            return;
        }

        String result = voucherDAO.claimVoucherWithReason(customer.getCustomerId(), code.trim());
        if ("ok".equals(result)) {
            response.sendRedirect(buildVoucherRedirect(request, "success", "claimed"));
            return;
        }
        response.sendRedirect(buildVoucherRedirect(request, "error", result));
    }

    private void showFreeVoucherPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        voucherDAO.expireOutdatedVouchers();
        List<Voucher> freeVouchers = voucherDAO.getFreeVouchers();
        request.setAttribute("freeVouchers", freeVouchers);

        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer != null) {
            Map<Integer, Boolean> claimedVoucherIds = voucherDAO.getClaimedVoucherIdMap(customer.getCustomerId());
            request.setAttribute("claimedVoucherIds", claimedVoucherIds);
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/voucherfree.jsp").forward(request, response);
    }

    private boolean isFreeVoucherRoute(HttpServletRequest request) {
        return "/voucher-free".equals(request.getServletPath());
    }

    private String buildVoucherRedirect(HttpServletRequest request, String paramName, String value) {
        String source = request.getParameter("source");
        String baseUrl;
        if ("home".equalsIgnoreCase(source)) {
            baseUrl = request.getContextPath() + "/home";
        } else if (isFreeVoucherRoute(request)) {
            baseUrl = request.getContextPath() + "/voucher-free";
        } else {
            baseUrl = request.getContextPath() + "/profile?action=view&tab=voucher";
        }

        return baseUrl
                + (baseUrl.contains("?") ? "&" : "?")
                + ("home".equalsIgnoreCase(source) ? "voucherPopup=1&" : "")
                + paramName
                + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
