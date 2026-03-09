package Controllers.guest;

import DALs.BrandDAO;
import DALs.CategoryDAO;
import Model.Brand;
import Model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class HeaderMenuController extends HttpServlet {

    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
        brandDAO = new BrandDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getAllCategories();
        List<Brand> brands = brandDAO.getAll();

        StringBuilder json = new StringBuilder();
        json.append("{\"categories\":[");

        boolean first = true;
        for (Category category : categories) {
            if (category == null || !category.isStatus()) {
                continue;
            }
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("{\"id\":").append(category.getCategoryId())
                .append(",\"name\":\"").append(escapeJson(category.getName())).append("\"}");
        }

        json.append("],\"brands\":[");
        first = true;
        for (Brand brand : brands) {
            if (brand == null || !brand.isStatus()) {
                continue;
            }
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("{\"id\":").append(brand.getBrandId())
                .append(",\"name\":\"").append(escapeJson(brand.getName())).append("\"}");
        }
        json.append("]}");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

