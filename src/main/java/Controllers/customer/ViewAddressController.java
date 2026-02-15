package Controllers.customer;

import DALs.AddressDAO;
import Model.Address;
import Model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;


@WebServlet("/address-list")
public class ViewAddressController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        AddressDAO dao = new AddressDAO();
        List<Address> list = dao.getAddressesByCustomerId(customer.getCustomerId());

        request.setAttribute("addressList", list);
        request.getRequestDispatcher("/views/customer/address-list.jsp")
               .forward(request, response);
    }
}
