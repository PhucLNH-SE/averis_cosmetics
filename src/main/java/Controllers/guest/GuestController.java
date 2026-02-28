package Controllers.guest;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GuestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {

            case "/home":
                request.getRequestDispatcher("/views/common/home.jsp")
                        .forward(request, response);
                break;

            case "/introduce":
                request.getRequestDispatcher("/views/guest/about-us.jsp")
                        .forward(request, response);
                break;

            case "/contact":
                request.getRequestDispatcher("/views/guest/contact.jsp")
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
        doGet(request, response);
    }
}