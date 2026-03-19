package Filters;

import Model.Manager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class ManagerAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String directViewRedirect = resolveDirectViewRedirect(path);
        if (directViewRedirect != null) {
            resp.sendRedirect(req.getContextPath() + directViewRedirect);
            return;
        }

        String requiredRole = resolveRequiredRole(path);

        if (requiredRole == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Manager manager = session == null ? null : (Manager) session.getAttribute("manager");
        String role = manager == null || manager.getManagerRole() == null
                ? "GUEST"
                : manager.getManagerRole().toUpperCase();

        if ("STAFF".equals(requiredRole)) {
            if (!"STAFF".equals(role) && !"ADMIN".equals(role)) {
                resp.sendRedirect(req.getContextPath() + "/manager-auth");
                return;
            }
        } else if (!requiredRole.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/manager-auth");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveRequiredRole(String path) {
        if (path.startsWith("/admin/") || path.startsWith("/views/admin/") || "/AdminControllers".equals(path)) {
            return "ADMIN";
        }
        if (path.startsWith("/staff/") || path.startsWith("/views/staff/") || "/StaffController".equals(path)) {
            return "STAFF";
        }
        return null;
    }

    private String resolveDirectViewRedirect(String path) {
        switch (path) {
            case "/views/admin/admin-panel.jsp":
            case "/views/admin/dashboard.jsp":
            case "/views/admin/partials/dashboard-content.jsp":
            case "/views/admin/partials/coming-soon-content.jsp":
                return "/admin/panel?view=dashboard";
            case "/views/admin/manage-users.jsp":
            case "/views/admin/partials/manage-users-content.jsp":
                return "/admin/panel?view=users";
            case "/views/admin/manage-brand.jsp":
            case "/views/admin/partials/manage-brand-content.jsp":
                return "/admin/manage-brand";
            case "/views/admin/manage-category.jsp":
            case "/views/admin/partials/manage-category-content.jsp":
                return "/admin/manage-category";
            case "/views/admin/manage-product.jsp":
            case "/views/admin/partials/manage-product-content.jsp":
                return "/admin/manage-product";
            case "/views/admin/import-product.jsp":
            case "/views/admin/partials/import-product-content.jsp":
                return "/admin/import-product?action=importproduct";
            case "/views/admin/manage-importproduct.jsp":
            case "/views/admin/import-detail.jsp":
            case "/views/admin/partials/manage-importproduct-content.jsp":
                return "/admin/import-product?action=history";
            case "/views/admin/manage-voucher.jsp":
            case "/views/admin/partials/manage-voucher-content.jsp":
                return "/admin/manage-voucher";
            case "/views/admin/manage-staff.jsp":
            case "/views/admin/partials/manage-staff-content.jsp":
            case "/views/admin/partials/manage-staff-detail.jsp":
                return "/admin/manage-staff";
            case "/views/admin/partials/manage-statistic-content.jsp":
                return "/admin/manage-statistic";
            case "/views/staff/staff-panel.jsp":
            case "/views/staff/dashboard.jsp":
            case "/views/staff/partials/dashboard-content.jsp":
                return "/staff/panel?view=dashboard";
            case "/views/staff/manage-orders.jsp":
            case "/views/staff/partials/manage-orders-content.jsp":
                return "/staff/manage-orders";
            case "/views/staff/manage-feedback.jsp":
            case "/views/staff/partials/manage-feedback-content.jsp":
            case "/views/staff/partials/manage-feedback-comments.jsp":
                return "/staff/manage-feedback";
            case "/views/staff/manage-brand.jsp":
            case "/views/staff/partials/manage-brand-content.jsp":
                return "/staff/manage-brand";
            case "/views/staff/manage-category.jsp":
            case "/views/staff/partials/manage-category-content.jsp":
                return "/staff/manage-category";
            default:
                return null;
        }
    }
}
