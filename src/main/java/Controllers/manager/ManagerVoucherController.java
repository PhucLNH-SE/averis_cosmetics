package Controllers.manager;

import DALs.VoucherDAO;
import Model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;

public class ManagerVoucherController extends HttpServlet {

    private static final Pattern VOUCHER_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,50}$");
    private static final BigDecimal MAX_PERCENT_DISCOUNT = new BigDecimal("100");
    private static final BigDecimal MAX_FIXED_DISCOUNT = new BigDecimal("999999999.99");
    private static final String ADMIN_LIST_URL = "/admin/manage-voucher";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-voucher-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-voucher-content.jsp";
    private VoucherDAO voucherDAO;

    @Override
    public void init() throws ServletException {
        voucherDAO = new VoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        if (isStaffRoute(request)) {
            switch (action) {
                case "list":
                default:
                    showManageVoucher(request, response, null, null);
                    break;
            }
        } else {
            switch (action) {
                case "edit":
                    showEditVoucherForm(request, response);
                    break;
                case "list":
                default:
                    showManageVoucher(request, response, null, null);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (isStaffRoute(request)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "create":
                createVoucher(request, response);
                break;
            case "update":
                updateVoucher(request, response);
                break;
            case "toggleHome":
                toggleVoucherHomeVisibility(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
                break;
        }
    }

    private void showManageVoucher(HttpServletRequest request, HttpServletResponse response,
            Voucher selectedVoucher, String formMode)
            throws ServletException, IOException {
        List<Voucher> vouchers = voucherDAO.getAll();
        request.setAttribute("vouchers", vouchers);
        request.setAttribute("selectedVoucher", selectedVoucher);
        request.setAttribute("formMode", formMode);
        request.setAttribute("currentView", "voucher");
        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private void showEditVoucherForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int voucherId = Integer.parseInt(idParam);
            Voucher selectedVoucher = voucherDAO.getById(voucherId);
            if (selectedVoucher == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }
            showManageVoucher(request, response, selectedVoucher, "update");
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void createVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Voucher voucher = buildVoucherFromRequest(request, false);
            validateVoucher(voucher);
            Voucher existed = voucherDAO.getByCode(voucher.getCode());
            if (existed != null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=duplicateCode");
                return;
            }

            boolean ok = voucherDAO.insert(voucher);
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=" + (ok ? "created" : "failed"));
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=invalidData");
        }
    }

    private void updateVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Voucher voucher = buildVoucherFromRequest(request, true);
            Voucher existed = voucherDAO.getByCode(voucher.getCode());
            if (existed != null && existed.getVoucherId() != voucher.getVoucherId()) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=duplicateCode");
                return;
            }

            Voucher old = voucherDAO.getById(voucher.getVoucherId());
            if (old == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }

            voucher.setClaimedQuantity(old.getClaimedQuantity());
            voucher.setCreatedAt(old.getCreatedAt());
            validateVoucher(voucher);
            boolean ok = voucherDAO.update(voucher);
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=" + (ok ? "updated" : "failed"));
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=invalidData");
        }
    }

    private void toggleVoucherHomeVisibility(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String voucherIdRaw = request.getParameter("voucherId");
        if (voucherIdRaw == null || voucherIdRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=toggleHomeFailed");
            return;
        }

        try {
            int voucherId = Integer.parseInt(voucherIdRaw);
            Voucher voucher = voucherDAO.getById(voucherId);
            if (voucher == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }

            boolean targetState = !Boolean.TRUE.equals(voucher.getShowonfreevoucher());
            boolean ok = voucherDAO.setShowOnHome(voucherId, targetState);
            if (!ok) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=toggleHomeFailed");
                return;
            }
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL
                    + "?success=" + (targetState ? "shownOnHome" : "hiddenFromHome"));
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=toggleHomeFailed");
        }
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getServletPath() != null && request.getServletPath().startsWith("/staff/");
    }

    private Voucher buildVoucherFromRequest(HttpServletRequest request, boolean includeId) {
        Voucher voucher = new Voucher();
        if (includeId) {
            voucher.setVoucherId(Integer.parseInt(request.getParameter("voucherId")));
        }

        String code = request.getParameter("code");
        String discountType = request.getParameter("discountType");
        String voucherType = request.getParameter("voucherType");
        String discountValue = request.getParameter("discountValue");
        String quantity = request.getParameter("quantity");
        String status = request.getParameter("status");
        String fixedStartAt = request.getParameter("fixedStartAt");
        String fixedEndAt = request.getParameter("fixedEndAt");
        String relativeDays = request.getParameter("relativeDays");

        voucher.setCode(code == null ? "" : code.trim());
        voucher.setDiscountType(discountType == null ? "" : discountType.trim().toUpperCase());
        voucher.setDiscountValue(new BigDecimal(discountValue));
        voucher.setQuantity(Integer.parseInt(quantity));
        voucher.setStatus(parseStatus(status));
        voucher.setVoucherType(voucherType == null ? "" : voucherType.trim().toUpperCase());
        voucher.setExpiredAt(null);

        switch (voucher.getVoucherType()) {
            case "FIXED_END_DATE":
                voucher.setFixedStartAt(parseDateTime(fixedStartAt));
                voucher.setFixedEndAt(parseDateTime(fixedEndAt));
                voucher.setRelativeDays(null);
                voucher.setExpiredAt(voucher.getFixedEndAt());
                break;
            case "RELATIVE_DAYS":
            default:
                voucher.setFixedStartAt(null);
                voucher.setFixedEndAt(null);
                voucher.setRelativeDays(Integer.valueOf(relativeDays));
                break;
        }

        voucher.setClaimedQuantity(0);
        voucher.setCreatedAt(LocalDateTime.now());
        return voucher;
    }

    private LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(raw);
    }

    private Boolean parseStatus(String rawStatus) {
        if ("1".equals(rawStatus)) {
            return Boolean.TRUE;
        }
        if ("0".equals(rawStatus)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Unsupported voucher status.");
    }

    void validateVoucher(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher data is required.");
        }
        String code = voucher.getCode() == null ? "" : voucher.getCode().trim();
        if (!VOUCHER_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Voucher code must be 3-50 characters and use only letters, numbers, _ or -.");
        }
        if (voucher.getStatus() == null) {
            throw new IllegalArgumentException("Voucher status is required.");
        }
        if (voucher.getDiscountValue() == null || voucher.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0.");
        }
        if (voucher.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer.");
        }

        String discountType = voucher.getDiscountType() == null ? "" : voucher.getDiscountType().toUpperCase();
        switch (discountType) {
            case "PERCENT":
                if (voucher.getDiscountValue().compareTo(BigDecimal.ONE) < 0
                        || voucher.getDiscountValue().compareTo(MAX_PERCENT_DISCOUNT) > 0) {
                    throw new IllegalArgumentException("Percent discount must be between 1 and 100.");
                }
                break;
            case "FIXED":
                if (voucher.getDiscountValue().compareTo(MAX_FIXED_DISCOUNT) > 0) {
                    throw new IllegalArgumentException("Fixed discount exceeds the system limit.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported discount type.");
        }

        if (voucher.getQuantity() < voucher.getClaimedQuantity()) {
            throw new IllegalArgumentException("Quantity cannot be smaller than claimed quantity.");
        }

        String voucherType = voucher.getVoucherType() == null ? "" : voucher.getVoucherType().toUpperCase();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        switch (voucherType) {
            case "FIXED_END_DATE":
                if (voucher.getFixedStartAt() == null || voucher.getFixedEndAt() == null) {
                    throw new IllegalArgumentException("Fixed voucher requires start and end date.");
                }
                if (!voucher.getFixedEndAt().isAfter(voucher.getFixedStartAt())) {
                    throw new IllegalArgumentException("End date must be after start date.");
                }
                if (!voucher.getFixedEndAt().isAfter(now)) {
                    throw new IllegalArgumentException("End date must be in the future.");
                }
                break;
            case "RELATIVE_DAYS":
                if (voucher.getRelativeDays() == null || voucher.getRelativeDays() <= 0) {
                    throw new IllegalArgumentException("Relative days must be greater than 0.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported voucher type.");
        }

        if (Boolean.TRUE.equals(voucher.getStatus())) {
            if (voucher.getQuantity() < voucher.getClaimedQuantity()) {
                throw new IllegalArgumentException("Cannot activate a voucher that is out of quantity.");
            }
            if ("FIXED_END_DATE".equals(voucherType) && !voucher.getFixedEndAt().isAfter(now)) {
                throw new IllegalArgumentException("Cannot activate an expired voucher.");
            }
        }
    }
}



