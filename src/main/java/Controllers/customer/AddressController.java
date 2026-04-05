package Controllers.customer;

import DALs.AddressDAO;
import Model.Address;
import Model.Customer;
import Utils.ValidationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AddressController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "view";
        }

        Customer customer = (Customer) session.getAttribute("customer");

        switch (action) {
            case "view":
            case "list":
                showAddressList(request, response, session, customer);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response, customer);
                break;
            default:
                redirectToAddressList(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "add";
        }

        Customer customer = (Customer) session.getAttribute("customer");

        switch (action) {
            case "add":
                addAddress(request, response, customer);
                break;
            case "edit":
                updateAddress(request, response, customer);
                break;
            case "delete":
                deleteAddress(request, response, customer);
                break;
            case "setdefault":
                setDefaultAddress(request, response, customer);
                break;
            default:
                redirectToAddressList(request, response);
                break;
        }
    }

    private void showAddressList(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            Customer sessionCustomer)
            throws ServletException, IOException {

        Customer customer = sessionCustomer;
        AddressDAO addressDAO = new AddressDAO();
        List<Address> addresses = addressDAO.getAddressesByCustomerId(customer.getCustomerId());

        request.setAttribute("customer", customer);
        request.setAttribute("addresses", addresses);
        request.setAttribute("tab", "address");
        consumeProfileFlashMessage(session, request);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/add-address.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String addressIdStr = request.getParameter("id");
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            setProfileFlashMessage(session, "Invalid address ID", "error");
            redirectToAddressList(request, response);
            return;
        }

        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.getAddressById(addressId);

            if (address == null || address.getCustomerId() != customer.getCustomerId()) {
                setProfileFlashMessage(session, "Address not found", "error");
                redirectToAddressList(request, response);
                return;
            }

            request.setAttribute("address", address);
            request.getRequestDispatcher("/WEB-INF/views/customer/edit-address.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            setProfileFlashMessage(session, "Invalid address ID format", "error");
            redirectToAddressList(request, response);
        }
    }

    private void addAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Address address = buildAddressFromRequest(request, customer.getCustomerId(), 0);
        try {
            ValidationUtil.validateAddressInput(address);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            showAddForm(request, response);
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        boolean success = addressDAO.insertAddress(address);

        if (success) {
            setProfileFlashMessage(session, "Address added successfully", "success");
        } else {
            setProfileFlashMessage(session, "Failed to add address", "error");
        }

        redirectToAddressList(request, response);
    }

    private void updateAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String addressIdStr = request.getParameter("id");
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            setProfileFlashMessage(session, "Invalid address ID", "error");
            redirectToAddressList(request, response);
            return;
        }

        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            Address existingAddress = addressDAO.getAddressById(addressId);

            if (existingAddress == null || existingAddress.getCustomerId() != customer.getCustomerId()) {
                setProfileFlashMessage(session, "Address not found", "error");
                redirectToAddressList(request, response);
                return;
            }

            Address address = buildAddressFromRequest(request, customer.getCustomerId(), addressId);
            try {
                ValidationUtil.validateAddressInput(address);
            } catch (IllegalArgumentException ex) {
                request.setAttribute("error", ex.getMessage());
                request.setAttribute("address", address);
                request.getRequestDispatcher("/WEB-INF/views/customer/edit-address.jsp").forward(request, response);
                return;
            }

            boolean success = addressDAO.updateAddress(address);

            if (success) {
                setProfileFlashMessage(session, "Address updated successfully", "success");
            } else {
                setProfileFlashMessage(session, "Failed to update address", "error");
            }

            redirectToAddressList(request, response);

        } catch (NumberFormatException e) {
            setProfileFlashMessage(session, "Invalid address ID format", "error");
            redirectToAddressList(request, response);
        }
    }

    private void deleteAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String addressIdStr = request.getParameter("id");

        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            setProfileFlashMessage(session, "Invalid address ID", "error");
            redirectToAddressList(request, response);
            return;
        }

        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            String result = addressDAO.deleteAddress(addressId, customer.getCustomerId());

            if ("success".equals(result)) {
                setProfileFlashMessage(session, "Address deleted successfully", "success");
            } else if ("not_found".equals(result)) {
                setProfileFlashMessage(session, "Address not found", "error");
            } else {
                setProfileFlashMessage(session, "Failed to delete address", "error");
            }

        } catch (NumberFormatException e) {
            setProfileFlashMessage(session, "Invalid address ID format", "error");
        }

        redirectToAddressList(request, response);
    }

    private void setDefaultAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String addressIdStr = request.getParameter("id");

        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            setProfileFlashMessage(session, "Invalid address ID", "error");
            redirectToAddressList(request, response);
            return;
        }

        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            boolean success = addressDAO.setDefaultAddress(addressId, customer.getCustomerId());

            if (success) {
                setProfileFlashMessage(session, "Default address updated successfully", "success");
            } else {
                setProfileFlashMessage(session, "Failed to set default address", "error");
            }

        } catch (NumberFormatException e) {
            setProfileFlashMessage(session, "Invalid address ID format", "error");
        }

        redirectToAddressList(request, response);
    }

    private void consumeProfileFlashMessage(HttpSession session, HttpServletRequest request) {
        if (session != null && session.getAttribute("profileMessage") != null) {
            request.setAttribute("profileMessage", session.getAttribute("profileMessage"));
            if (session.getAttribute("profileMessageType") != null) {
                request.setAttribute("profileMessageType", session.getAttribute("profileMessageType"));
                session.removeAttribute("profileMessageType");
            }
            session.removeAttribute("profileMessage");
        }
    }

    private void redirectToAddressList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/address");
    }

    private Address buildAddressFromRequest(HttpServletRequest request, int customerId, int addressId) {
        return ValidationUtil.normalizeAddress(
                customerId,
                addressId,
                request.getParameter("receiverName"),
                request.getParameter("phone"),
                request.getParameter("province"),
                request.getParameter("district"),
                request.getParameter("ward"),
                request.getParameter("streetAddress"),
                parseChecked(request.getParameter("isDefault")));
    }

    private boolean parseChecked(String value) {
        return "on".equalsIgnoreCase(value)
                || "1".equals(value)
                || "true".equalsIgnoreCase(value);
    }

    private void setProfileFlashMessage(HttpSession session, String message, String type) {
        if (session == null) {
            return;
        }
        session.setAttribute("profileMessage", message);
        session.setAttribute("profileMessageType", type);
    }
}


