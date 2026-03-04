package Controllers.staff;

import java.io.IOException;
import java.util.List;

import DALs.BrandDAO;
import Model.Brand;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BrandController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BrandDAO dao = new BrandDAO();
        List<Brand> brands = dao.getAll();
        request.setAttribute("brands", brands);
        request.getRequestDispatcher("/views/staff/manage-brand.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
