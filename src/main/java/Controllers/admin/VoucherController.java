package Controllers.admin;

import DALs.VoucherDAO;
import Model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VoucherController extends HttpServlet {

    private VoucherDAO voucherDAO;

    @Override
    public void init() throws ServletException {
        voucherDAO = new VoucherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/admin/create-voucher":
                request.setAttribute("formMode", "create");
                showManageVoucher(request, response);
                break;
            case "/admin/update-voucher":
                request.setAttribute("formMode", "update");
                showManageVoucher(request, response);
                break;
            case "/admin/manage-voucher":
            default:
                showManageVoucher(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "create":
                createVoucher(request, response);
                break;
            case "update":
                updateVoucher(request, response);
                break;
            case "delete":
                deleteVoucher(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/manage-voucher");
                break;
        }
    }

    private void showManageVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Voucher> vouchers = voucherDAO.getAll();
        request.setAttribute("vouchers", vouchers);
        request.setAttribute("currentView", "voucher");
        request.setAttribute("contentPage", "/views/admin/partials/manage-voucher-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }

    private void createVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Voucher voucher = buildVoucherFromRequest(request, false);
            validateVoucher(voucher);
            Voucher existed = voucherDAO.getByCode(voucher.getCode());
            if (existed != null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=duplicateCode");
                return;
            }

            boolean ok = voucherDAO.insert(voucher);
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?success=" + (ok ? "created" : "failed"));
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=invalidData");
        }
    }

    private void updateVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Voucher voucher = buildVoucherFromRequest(request, true);
            validateVoucher(voucher);
            Voucher existed = voucherDAO.getByCode(voucher.getCode());
            if (existed != null && existed.getVoucherId() != voucher.getVoucherId()) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=duplicateCode");
                return;
            }

            Voucher old = voucherDAO.getById(voucher.getVoucherId());
            if (old == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=notFound");
                return;
            }

            voucher.setClaimedQuantity(old.getClaimedQuantity());
            voucher.setCreatedAt(old.getCreatedAt());
            boolean ok = voucherDAO.update(voucher);
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?success=" + (ok ? "updated" : "failed"));
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=invalidData");
        }
    }

    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idRaw = request.getParameter("voucherId");
        try {
            int id = Integer.parseInt(idRaw);
            boolean ok = voucherDAO.softDelete(id);
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?success=" + (ok ? "deleted" : "failed"));
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-voucher?error=invalidId");
        }
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
        voucher.setDiscountType(discountType);
        voucher.setDiscountValue(new BigDecimal(discountValue));
        voucher.setQuantity(Integer.parseInt(quantity));
        voucher.setStatus("1".equals(status));
        voucher.setVoucherType(voucherType);
        voucher.setExpiredAt(null);

        switch (voucherType) {
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

    private void validateVoucher(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher data is required.");
        }
        if (voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Voucher code is required.");
        }
        if (voucher.getDiscountValue() == null || voucher.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than 0.");
        }
        if (voucher.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0.");
        }
        if (voucher.getQuantity() == 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        String voucherType = voucher.getVoucherType() == null ? "" : voucher.getVoucherType().toUpperCase();
        switch (voucherType) {
            case "FIXED_END_DATE":
                if (voucher.getFixedStartAt() == null || voucher.getFixedEndAt() == null) {
                    throw new IllegalArgumentException("Fixed voucher requires start and end date.");
                }
                if (voucher.getFixedEndAt().isBefore(voucher.getFixedStartAt())) {
                    throw new IllegalArgumentException("End date cannot be before start date.");
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
    }
}
