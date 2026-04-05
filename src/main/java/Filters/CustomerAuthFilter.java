package Filters;

import DALs.CustomerDAO;
import Model.Customer;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "CustomerAuthFilter",
        urlPatterns = {
                "/profile",
                "/checkout",
                "/order/*",
                "/address",
                "/address/*",
                "/address-api"
        })
public class CustomerAuthFilter implements Filter {

    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");

        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Customer currentCustomer = customerDAO.getCustomerById(customer.getCustomerId());
        if (currentCustomer == null
                || currentCustomer.getStatus() == null
                || !currentCustomer.getStatus()) {
            session.invalidate();

            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        session.setAttribute("customer", currentCustomer);

        chain.doFilter(request, response);
    }
}
