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

        switch (path) {
            case "/admin/manage-category":
                forwardManageCategory(request, response);
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
                String name = request.getParameter("name");
                String statusParam = request.getParameter("status");
                boolean status = "on".equalsIgnoreCase(statusParam)
                        || "1".equals(statusParam)
                        || "true".equalsIgnoreCase(statusParam);
                if (name != null && !name.trim().isEmpty()) {
                    dao.addCategory(name.trim(), status);
                }
                response.sendRedirect(request.getContextPath() + "/admin/manage-category?success=add");
                break;
            }
            case "/admin/update-category": {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    String statusParam = request.getParameter("status");
                    boolean status = "on".equalsIgnoreCase(statusParam)
                            || "1".equals(statusParam)
                            || "true".equalsIgnoreCase(statusParam);
                    if (name != null && !name.trim().isEmpty()) {
                        dao.updateCategory(id, name.trim(), status);
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?success=update");
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=updateFailed");
                }
                break;
            }
            case "/admin/delete-category":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.deleteCategory(id);
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?success=delete");
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/manage-category?error=deleteFailed");
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void forwardManageCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();
        request.setAttribute("categories", categories);
        request.setAttribute("currentView", "categories");
        request.setAttribute("contentPage", "/views/admin/partials/manage-category-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }
}
