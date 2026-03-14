package Controllers.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StaffPanelController extends HttpServlet {

    private static final String DEFAULT_VIEW = "dashboard";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        if (view == null || view.trim().isEmpty()) {
            view = DEFAULT_VIEW;
        }

        view = view.trim().toLowerCase();
        request.setAttribute("currentView", view);

        switch (view) {
            case "orders":
                response.sendRedirect(request.getContextPath() + "/staff/manage-orders");
                return;
            case "feedback":
                response.sendRedirect(request.getContextPath() + "/staff/manage-feedback");
                return;
            case "brands":
                response.sendRedirect(request.getContextPath() + "/staff/manage-brand");
                return;
            case "categories":
                response.sendRedirect(request.getContextPath() + "/staff/manage-category");
                return;
            case "dashboard":
            default:
                request.setAttribute("currentView", DEFAULT_VIEW);
                request.setAttribute("contentPage", "/views/staff/partials/dashboard-content.jsp");
                break;
        }

        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }
}
