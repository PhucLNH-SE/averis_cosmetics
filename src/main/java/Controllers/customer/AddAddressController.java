/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.customer;

import DALs.AddressDAO;
import Model.Address;
import Model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/add-address")
public class AddAddressController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/customer/add-address.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        Address a = new Address();
        a.setCustomerId(customer.getCustomerId());
        a.setReceiverName(request.getParameter("receiverName"));
        a.setPhone(request.getParameter("phone"));
        a.setProvince(request.getParameter("province"));
        a.setDistrict(request.getParameter("district"));
        a.setWard(request.getParameter("ward"));
        a.setStreetAddress(request.getParameter("streetAddress"));
        a.setDefault(request.getParameter("isDefault") != null);

        AddressDAO dao = new AddressDAO();
        dao.addAddress(a);

        response.sendRedirect(request.getContextPath() + "/address-list");
    }
}

