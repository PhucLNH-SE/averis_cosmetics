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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VoucherDAO voucherDAO = new VoucherDAO();
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            switch (request.getServletPath()) {
                case "/voucher-free":
                    action = "view";
                    break;
                case "/my-voucher":
                default:
                    action = "redirectProfile";
                    break;
            }
        } else {
            action = action.trim();
        }

        switch (action) {
            case "view":
                showFreeVoucherPage(request, response, voucherDAO);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VoucherDAO voucherDAO = new VoucherDAO();
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "claim";
        } else {
            action = action.trim();
        }

        switch (action) {
            case "claim":
                handleClaimVoucher(request, response, voucherDAO);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/profile?action=view&tab=voucher");
                break;
        }
    }

    private void handleClaimVoucher(HttpServletRequest request, HttpServletResponse response, VoucherDAO voucherDAO)
            throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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

    private void showFreeVoucherPage(HttpServletRequest request, HttpServletResponse response, VoucherDAO voucherDAO)
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

    private String buildVoucherRedirect(HttpServletRequest request, String paramName, String value) {
        String baseUrl = getVoucherRedirectBaseUrl(request);
        return baseUrl
                + (baseUrl.contains("?") ? "&" : "?")
                + paramName
                + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getVoucherRedirectBaseUrl(HttpServletRequest request) {
        String source = request.getParameter("source");

        switch (source == null ? "" : source.trim()) {
            case "home":
                return request.getContextPath() + "/home?voucherPopup=1";
            case "voucher-free":
                return request.getContextPath() + "/voucher-free";
            case "profile":
            default:
                return request.getContextPath() + "/profile?action=view&tab=voucher";
        }
    }
}
