package Controllers.customer;

import DALs.AddressDAO;
import Model.Address;
import Model.Customer;
import java.io.IOException;
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
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        Customer customer = (Customer) session.getAttribute("customer");
        
        switch (action) {
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response, customer);
                break;
            case "delete":
                deleteAddress(request, response, customer);
                break;
            case "setdefault":
                setDefaultAddress(request, response, customer);
                break;
            case "list":
            default:
                response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "add";
        
        Customer customer = (Customer) session.getAttribute("customer");
        
        switch (action) {
            case "add":
                addAddress(request, response, customer);
                break;
            case "edit":
                updateAddress(request, response, customer);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
                break;
        }
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/customer/add-address.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String addressIdStr = request.getParameter("id");
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            session.setAttribute("profileMessage", "Invalid address ID");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
            return;
        }
        
        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.getAddressById(addressId);
            
            if (address == null || address.getCustomerId() != customer.getCustomerId()) {
                session.setAttribute("profileMessage", "Address not found");
                response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
                return;
            }
            
            request.setAttribute("address", address);
            request.getRequestDispatcher("/views/customer/edit-address.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            session.setAttribute("profileMessage", "Invalid address ID format");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
        }
    }
    
    private void addAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        String receiverName = request.getParameter("receiverName");
        String phone = request.getParameter("phone");
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String ward = request.getParameter("ward");
        String streetAddress = request.getParameter("streetAddress");
        String isDefaultStr = request.getParameter("isDefault");
        
        // Validation
        if (receiverName == null || receiverName.trim().isEmpty()) {
            request.setAttribute("error", "Receiver name is required");
            showAddForm(request, response);
            return;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Phone is required");
            showAddForm(request, response);
            return;
        }
        
        if (province == null || province.trim().isEmpty()) {
            request.setAttribute("error", "Province is required");
            showAddForm(request, response);
            return;
        }
        
        if (district == null || district.trim().isEmpty()) {
            request.setAttribute("error", "District is required");
            showAddForm(request, response);
            return;
        }
        
        if (ward == null || ward.trim().isEmpty()) {
            request.setAttribute("error", "Ward is required");
            showAddForm(request, response);
            return;
        }
        
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            request.setAttribute("error", "Street address is required");
            showAddForm(request, response);
            return;
        }
        
        boolean isDefault = "on".equals(isDefaultStr) || "1".equals(isDefaultStr);
        
        Address address = new Address();
        address.setCustomerId(customer.getCustomerId());
        address.setReceiverName(receiverName.trim());
        address.setPhone(phone.trim());
        address.setProvince(province.trim());
        address.setDistrict(district.trim());
        address.setWard(ward.trim());
        address.setStreetAddress(streetAddress.trim());
        address.setIsDefault(isDefault);
        
        AddressDAO addressDAO = new AddressDAO();
        boolean success = addressDAO.insertAddress(address);
        
        if (success) {
            if (isDefault) {
                addressDAO.setDefaultAddress(address.getAddressId(), customer.getCustomerId());
            }
            session.setAttribute("profileMessage", "Address added successfully");
        } else {
            session.setAttribute("profileMessage", "Failed to add address");
        }
        
        response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
    }
    
    private void updateAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        String addressIdStr = request.getParameter("id");
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            session.setAttribute("profileMessage", "Invalid address ID");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
            return;
        }
        
        try {
            int addressId = Integer.parseInt(addressIdStr);
            
            String receiverName = request.getParameter("receiverName");
            String phone = request.getParameter("phone");
            String province = request.getParameter("province");
            String district = request.getParameter("district");
            String ward = request.getParameter("ward");
            String streetAddress = request.getParameter("streetAddress");
            String isDefaultStr = request.getParameter("isDefault");
            
            // Validation
            if (receiverName == null || receiverName.trim().isEmpty()) {
                request.setAttribute("error", "Receiver name is required");
                request.setAttribute("addressId", addressId);
                showEditForm(request, response, customer);
                return;
            }
            
            if (phone == null || phone.trim().isEmpty()) {
                request.setAttribute("error", "Phone is required");
                request.setAttribute("addressId", addressId);
                showEditForm(request, response, customer);
                return;
            }
            
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.getAddressById(addressId);
            
            if (address == null || address.getCustomerId() != customer.getCustomerId()) {
                session.setAttribute("profileMessage", "Address not found");
                response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
                return;
            }
            
            boolean isDefault = "on".equals(isDefaultStr) || "1".equals(isDefaultStr);
            
            address.setReceiverName(receiverName.trim());
            address.setPhone(phone.trim());
            address.setProvince(province.trim());
            address.setDistrict(district.trim());
            address.setWard(ward.trim());
            address.setStreetAddress(streetAddress.trim());
            address.setIsDefault(isDefault);
            
            boolean success = addressDAO.updateAddress(address);
            
            if (success) {
                if (isDefault) {
                    addressDAO.setDefaultAddress(address.getAddressId(), customer.getCustomerId());
                }
                session.setAttribute("profileMessage", "Address updated successfully");
            } else {
                session.setAttribute("profileMessage", "Failed to update address");
            }
            
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
            
        } catch (NumberFormatException e) {
            session.setAttribute("profileMessage", "Invalid address ID format");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
        }
    }
    
    private void deleteAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String addressIdStr = request.getParameter("id");
        
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            session.setAttribute("profileMessage", "Invalid address ID");
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
            return;
        }
        
        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            boolean success = addressDAO.deleteAddress(addressId, customer.getCustomerId());
            
            if (success) {
                session.setAttribute("profileMessage", "Address deleted successfully");
            } else {
                session.setAttribute("profileMessage", "Failed to delete address");
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("profileMessage", "Invalid address ID format");
        }
        
        response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
    }
    
    private void setDefaultAddress(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        
        String addressIdStr = request.getParameter("id");
        
        if (addressIdStr == null || addressIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
            return;
        }
        
        try {
            int addressId = Integer.parseInt(addressIdStr);
            AddressDAO addressDAO = new AddressDAO();
            addressDAO.setDefaultAddress(addressId, customer.getCustomerId());
            
        } catch (NumberFormatException e) {
            // Silent fail - no message shown
        }
        
        response.sendRedirect(request.getContextPath() + "/CustomerController?action=view&tab=address");
    }
}