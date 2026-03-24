package Controllers.admin;

import DALs.CategoryDAO;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String action = request.getParameter("action");

        switch (path) {
            case "/admin/manage-category":
                if ("edit".equalsIgnoreCase(action)) {
                    showEditCategoryForm(request, response);
                } else {
                    forwardManageCategory(request, response, null, null, null);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        CategoryDAO dao = new CategoryDAO();

        switch (path) {
            case "/admin/add-category": {
                String name = trimToNull(request.getParameter("name"));
                boolean status = parseStatus(request.getParameter("status"));
                if (name == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=addFailed");
                    break;
                }
                if (dao.existsByName(name)) {
                    forwardManageCategory(request, response, null, null, "Category name already exists.");
                    return;
                }
                boolean added = dao.addCategory(name, status);
                response.sendRedirect(request.getContextPath()
                        + (added ? "/admin/manage-category?success=add"
                                : "/admin/manage-category?error=addFailed"));
                break;
            }
            case "/admin/update-category": {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = trimToNull(request.getParameter("name"));
                    boolean status = parseStatus(request.getParameter("status"));
                    if (name == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=updateFailed");
                        break;
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
                            + (updated ? "/admin/manage-category?success=update"
                                    : "/admin/manage-category?error=updateFailed"));
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=updateFailed");
                }
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void showEditCategoryForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=notFound");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            CategoryDAO dao = new CategoryDAO();
            Category selectedCategory = dao.getCategoryById(id);
            if (selectedCategory == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=notFound");
                return;
            }
            forwardManageCategory(request, response, selectedCategory, "update", null);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=notFound");
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
        request.setAttribute("contentPage", "/WEB-INF/views/admin/partials/manage-category-content.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-panel.jsp").forward(request, response);
    }
    private boolean parseStatus(String statusParam) {
        return "on".equalsIgnoreCase(statusParam)
                || "1".equals(statusParam)
                || "true".equalsIgnoreCase(statusParam);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
