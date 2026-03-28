package Controllers.manager;

import DALs.CategoryDAO;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ManagerCategoryController extends HttpServlet {

    private static final String ADMIN_LIST_URL = "/admin/manage-category";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-category-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-category-content.jsp";

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
                    showCategoryList(request, response);
                    break;
            }
        } else {
            switch (action) {
                case "edit":
                    showEditCategoryForm(request, response);
                    break;
                case "list":
                default:
                    showCategoryList(request, response);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        if (isStaffRoute(request)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        CategoryDAO dao = new CategoryDAO();
        switch (action) {
            case "add":
                addCategory(request, response, dao);
                break;
            case "update":
                updateCategory(request, response, dao);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
                break;
        }
    }

    private void showCategoryList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardManageCategory(request, response, null, null, null);
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response, CategoryDAO dao)
            throws ServletException, IOException {
        String name = trimToNull(request.getParameter("name"));
        boolean status = parseStatus(request.getParameter("status"));

        if (name == null) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=addFailed");
            return;
        }
        if (dao.existsByName(name)) {
            forwardManageCategory(request, response, null, null, "Category name already exists.");
            return;
        }

        boolean added = dao.insertCategory(name, status);
        response.sendRedirect(request.getContextPath()
                + (added ? ADMIN_LIST_URL + "?success=add"
                        : ADMIN_LIST_URL + "?error=addFailed"));
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response, CategoryDAO dao)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = trimToNull(request.getParameter("name"));
            boolean status = parseStatus(request.getParameter("status"));

            if (name == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=updateFailed");
                return;
            }
            if (dao.existsByNameExceptId(name, id)) {
                Category selectedCategory = new Category();
                selectedCategory.setCategoryId(id);
                selectedCategory.setName(name);
                selectedCategory.setStatus(status);
                forwardManageCategory(request, response, selectedCategory, "update", "Category name already exists.");
                return;
            }

            boolean updated = dao.updateCategory(id, name, status);
            response.sendRedirect(request.getContextPath()
                    + (updated ? ADMIN_LIST_URL + "?success=update"
                            : ADMIN_LIST_URL + "?error=updateFailed"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=updateFailed");
        }
    }

    private void showEditCategoryForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            CategoryDAO dao = new CategoryDAO();
            Category selectedCategory = dao.getCategoryById(id);
            if (selectedCategory == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }
            forwardManageCategory(request, response, selectedCategory, "update", null);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void forwardManageCategory(HttpServletRequest request, HttpServletResponse response,
            Category selectedCategory, String formMode, String error)
            throws ServletException, IOException {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();
        request.setAttribute("categories", categories);
        request.setAttribute("selectedCategory", selectedCategory);
        request.setAttribute("formMode", formMode);
        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }
        request.setAttribute("currentView", "categories");
        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

    private boolean parseStatus(String statusParam) {
        return "on".equalsIgnoreCase(statusParam)
                || "1".equals(statusParam)
                || "true".equalsIgnoreCase(statusParam);
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getServletPath() != null && request.getServletPath().startsWith("/staff/");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


