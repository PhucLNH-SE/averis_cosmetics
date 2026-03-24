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
        if (path.startsWith("/admin/") || "/AdminControllers".equals(path)) {
            return "ADMIN";
        }
        if (path.startsWith("/staff/") || "/StaffController".equals(path)) {
            return "STAFF";
        }
        return null;
    }
}
