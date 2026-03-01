package Controllers.admin;

import java.io.IOException;
import java.util.List;

import DALs.CategoryDAO;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CatagoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/manage-category":
                CategoryDAO dao = new CategoryDAO();
                List<Category> categories = dao.getAllCategories();
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("/views/admin/manage-category.jsp")
                        .forward(request, response);
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
                boolean status = "on".equalsIgnoreCase(statusParam) || "1".equals(statusParam) || "true".equalsIgnoreCase(statusParam);
                if (name != null && !name.trim().isEmpty()) {
                    dao.addCategory(name.trim(), status);
                }
                response.sendRedirect(request.getContextPath() + "/admin/manage-category");
                break;
            }

            case "/admin/update-category": {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String uname = request.getParameter("name");
                    String statusParam = request.getParameter("status");
                    boolean status = "on".equalsIgnoreCase(statusParam) || "1".equals(statusParam) || "true".equalsIgnoreCase(statusParam);
                    if (uname != null && !uname.trim().isEmpty()) {
                        dao.updateCategory(id, uname.trim(), status);
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
                response.sendRedirect(request.getContextPath() + "/admin/manage-category");
                break;
            }

            case "/admin/delete-category":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.deleteCategory(id);
                } catch (NumberFormatException e) {
                    // ignore
                }
                response.sendRedirect(request.getContextPath() + "/admin/manage-category");
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
