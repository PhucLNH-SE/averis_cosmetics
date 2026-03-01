package Controllers.admin;

import java.io.IOException;
import java.util.List;

import DALs.CategoryDAO;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CategoryController extends HttpServlet {

    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listCategories(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteCategory(request, response);
                break;
            default:
                listCategories(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            saveCategory(request, response);
        } else if ("update".equals(action)) {
            updateCategory(request, response);
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/views/admin/manage-category.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/admin/add-category.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int categoryId = Integer.parseInt(request.getParameter("id"));
        Category category = categoryDAO.getCategoryById(categoryId);
        request.setAttribute("category", category);
        request.getRequestDispatcher("/views/admin/edit-category.jsp").forward(request, response);
    }

    private void saveCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("name");
        String statusParam = request.getParameter("status");
        boolean status = "1".equals(statusParam) || "on".equals(statusParam);

        Category category = new Category();
        category.setName(name);
        category.setStatus(status);

        if (categoryDAO.addCategory(category)) {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list&success=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=add&error=1");
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String name = request.getParameter("name");
        String statusParam = request.getParameter("status");
        boolean status = "1".equals(statusParam) || "on".equals(statusParam);

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName(name);
        category.setStatus(status);

        if (categoryDAO.updateCategory(category)) {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list&success=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=edit&id=" + categoryId + "&error=1");
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int categoryId = Integer.parseInt(request.getParameter("id"));

        if (categoryDAO.deleteCategory(categoryId)) {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list&success=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/category?action=list&error=1");
        }
    }
}
