package Controllers.admin;

import DALs.BrandDAO;
import Model.Brand;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            case "delete":
                deleteBrand(request, response);
                break;
            default:
                listBrands(request, response);
                break;
        }
    }

    private void listBrands(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Brand> brands = brandDAO.getAll();
        request.setAttribute("brands", brands);
        request.getRequestDispatcher("/views/admin/manage-brand.jsp").forward(request, response);
    }

    private void addBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        boolean status = request.getParameter("status") != null && request.getParameter("status").equals("1");

        // Kiểm tra tên đã tồn tại chưa
        if (brandDAO.existsByName(name)) {
            request.setAttribute("error", "Tên thương hiệu đã tồn tại!");
            List<Brand> brands = brandDAO.getAll();
            request.setAttribute("brands", brands);
            request.getRequestDispatcher("/views/admin/manage-brand.jsp").forward(request, response);
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
        boolean status = request.getParameter("status") != null && request.getParameter("status").equals("1");

        // Kiểm tra tên đã tồn tại chưa (trừ brand hiện tại)
        if (brandDAO.existsByNameExceptId(name, brandId)) {
            request.setAttribute("error", "Tên thương hiệu đã tồn tại!");
            List<Brand> brands = brandDAO.getAll();
            request.setAttribute("brands", brands);
            request.getRequestDispatcher("/views/admin/manage-brand.jsp").forward(request, response);
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

    private void deleteBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int brandId = Integer.parseInt(request.getParameter("brandId"));

        boolean success = brandDAO.delete(brandId);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?success=delete");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/manage-brand?error=deleteFailed");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
    // </editor-fold>
}
