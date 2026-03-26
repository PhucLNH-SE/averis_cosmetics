package Controllers.manager;

import DALs.BrandDAO;
import Model.Brand;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ManagerBrandController extends HttpServlet {

    private BrandDAO brandDAO;
    private static final String ADMIN_LIST_URL = "/admin/manage-brand";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String STAFF_PANEL = "/WEB-INF/views/staff/staff-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-brand-content.jsp";
    private static final String STAFF_CONTENT = "/WEB-INF/views/staff/partials/manage-brand-content.jsp";

    @Override
    public void init() throws ServletException {
        brandDAO = new BrandDAO();
    }

    private void listBrands(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardManageBrand(request, response, null, null, null);
    }

    private void loadBrandForUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String brandIdParam = request.getParameter("id");
        if (brandIdParam == null || brandIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int brandId = Integer.parseInt(brandIdParam);
            Brand selectedBrand = brandDAO.getById(brandId);
            if (selectedBrand == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }
            forwardManageBrand(request, response, selectedBrand, "update", null);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void addBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        boolean status = "1".equals(request.getParameter("status"));

        if (brandDAO.existsByName(name)) {
            forwardManageBrand(request, response, null, null, "Brand name already exists.");
            return;
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setStatus(status);

        boolean success = brandDAO.insert(brand);
        if (success) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=add");
        } else {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=addFailed");
        }
    }

    private void updateBrand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int brandId = Integer.parseInt(request.getParameter("brandId"));
        String name = request.getParameter("name").trim();
        boolean status = "1".equals(request.getParameter("status"));

        if (brandDAO.existsByNameExceptId(name, brandId)) {
            Brand selectedBrand = new Brand();
            selectedBrand.setBrandId(brandId);
            selectedBrand.setName(name);
            selectedBrand.setStatus(status);
            forwardManageBrand(request, response, selectedBrand, "update", "Brand name already exists.");
            return;
        }

        Brand brand = new Brand();
        brand.setBrandId(brandId);
        brand.setName(name);
        brand.setStatus(status);

        boolean success = brandDAO.update(brand);
        if (success) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=update");
        } else {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=updateFailed");
        }
    }

    private void forwardManageBrand(HttpServletRequest request, HttpServletResponse response,
            Brand selectedBrand, String formMode, String error)
            throws ServletException, IOException {
        List<Brand> brands = brandDAO.getAll();
        request.setAttribute("brands", brands);
        request.setAttribute("selectedBrand", selectedBrand);
        request.setAttribute("formMode", formMode);
        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }
        request.setAttribute("currentView", "brands");
        boolean staffRoute = isStaffRoute(request);
        request.setAttribute("contentPage", staffRoute ? STAFF_CONTENT : ADMIN_CONTENT);
        request.getRequestDispatcher(staffRoute ? STAFF_PANEL : ADMIN_PANEL).forward(request, response);
    }

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
                    listBrands(request, response);
                    break;
            }
        } else {
            switch (action) {
                case "edit":
                    loadBrandForUpdate(request, response);
                    break;
                case "list":
                default:
                    listBrands(request, response);
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

        switch (action) {
            case "add":
                addBrand(request, response);
                break;
            case "update":
                updateBrand(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
                break;
        }
    }

    private boolean isStaffRoute(HttpServletRequest request) {
        return request.getServletPath() != null && request.getServletPath().startsWith("/staff/");
    }

    @Override
    public String getServletInfo() {
        return "Brand Controller";
    }
}



