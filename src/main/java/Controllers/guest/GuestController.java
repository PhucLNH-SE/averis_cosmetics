package Controllers.guest;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "GuestController", urlPatterns = {"/introduce", "/contact"})
public class GuestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length()).toLowerCase();
        
        if (pathInfo.contains("/introduce") || pathInfo.equals("/introduce")) {
            // Forward to About Us page
            request.getRequestDispatcher("/views/guest/about-us.jsp").forward(request, response);
        } else if (pathInfo.contains("/contact") || pathInfo.equals("/contact")) {
            // Forward to Contact page (will create later if needed)
            request.getRequestDispatcher("/views/guest/contact.jsp").forward(request, response);
        } else {
            // Default to home page if route doesn't match
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}