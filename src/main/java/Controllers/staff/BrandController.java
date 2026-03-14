package Controllers.staff;

import DALs.BrandDAO;
import Model.Brand;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BrandDAO dao = new BrandDAO();
        List<Brand> brands = dao.getAll();
        request.setAttribute("brands", brands);
        request.setAttribute("currentView", "brands");
        request.setAttribute("contentPage", "/views/staff/partials/manage-brand-content.jsp");
        request.getRequestDispatcher("/views/staff/staff-panel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
