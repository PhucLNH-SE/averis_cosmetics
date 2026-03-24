package Controllers.admin;

import DALs.BrandDAO;
import Model.Brand;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandController extends HttpServlet {

    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        brandDAO = new BrandDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listBrands(request, response);
                break;
            case "add":
                addBrand(request, response);
                break;
            case "update":
                updateBrand(request, response);
                break;
            default:
                listBrands(request, response);
                break;
        }
    }

    private void listBrands(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardManageBrand(request, response, null);
    }

    private void addBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        boolean status = "1".equals(request.getParameter("status"));

        if (brandDAO.existsByName(name)) {
            forwardManageBrand(request, response, "Brand name already exists.");
            return;
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setStatus(status);

        boolean success = brandDAO.insert(brand);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?success=add");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?error=addFailed");
        }
    }

    private void updateBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int brandId = Integer.parseInt(request.getParameter("brandId"));
        String name = request.getParameter("name").trim();
        boolean status = "1".equals(request.getParameter("status"));

        if (brandDAO.existsByNameExceptId(name, brandId)) {
            forwardManageBrand(request, response, "Brand name already exists.");
            return;
        }

        Brand brand = new Brand();
        brand.setBrandId(brandId);
        brand.setName(name);
        brand.setStatus(status);

        boolean success = brandDAO.update(brand);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?success=update");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?error=updateFailed");
        }
    }

    private void forwardManageBrand(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        List<Brand> brands = brandDAO.getAll();
        request.setAttribute("brands", brands);
        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }
        request.setAttribute("currentView", "brands");
        request.setAttribute("contentPage", "/views/admin/partials/manage-brand-content.jsp");
        request.getRequestDispatcher("/views/admin/admin-panel.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Brand Controller";
    }
}
