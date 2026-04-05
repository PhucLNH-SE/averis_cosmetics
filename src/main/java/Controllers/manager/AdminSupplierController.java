package Controllers.manager;

import DALs.SupplierDAO;
import Model.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminSupplierController extends HttpServlet {

    private static final String ADMIN_LIST_URL = "/admin/manage-supplier";
    private static final String ADMIN_PANEL = "/WEB-INF/views/admin/admin-panel.jsp";
    private static final String ADMIN_CONTENT = "/WEB-INF/views/admin/partials/manage-supplier-content.jsp";
    private static final String PHONE_REGEX = "^(?:0|84|\\+84)(?:3|5|7|8|9)\\d{8}$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "add":
                showAddSupplierForm(request, response);
                break;
            case "edit":
                showEditSupplierForm(request, response);
                break;
            case "list":
            default:
                showSupplierList(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        SupplierDAO supplierDAO = new SupplierDAO();
        switch (action) {
            case "add":
                addSupplier(request, response, supplierDAO);
                break;
            case "update":
                updateSupplier(request, response, supplierDAO);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
                break;
        }
    }

    private void showSupplierList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardManageSupplier(request, response, null, null, null, false);
    }

    private void showAddSupplierForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Supplier supplier = new Supplier();
        supplier.setStatus(true);
        forwardManageSupplier(request, response, supplier, "add", null, true);
    }

    private void showEditSupplierForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int supplierId = Integer.parseInt(idParam);
            SupplierDAO supplierDAO = new SupplierDAO();
            Supplier selectedSupplier = supplierDAO.getSupplierById(supplierId);

            if (selectedSupplier == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }

            forwardManageSupplier(request, response, selectedSupplier, "update", null, true);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void addSupplier(HttpServletRequest request, HttpServletResponse response, SupplierDAO supplierDAO)
            throws ServletException, IOException {
        Supplier supplier = buildSupplierFromRequest(request, 0);
        String validationError = validateSupplier(supplier);

        if (validationError != null) {
            forwardManageSupplier(request, response, supplier, "add", validationError, true);
            return;
        }
        if (supplierDAO.existsByName(supplier.getName())) {
            forwardManageSupplier(request, response, supplier, "add", "Supplier name already exists.", true);
            return;
        }

        boolean success = supplierDAO.insertSupplier(supplier);
        if (success) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=add");
            return;
        }

        forwardManageSupplier(request, response, supplier, "add", "Failed to add supplier.", true);
    }

    private void updateSupplier(HttpServletRequest request, HttpServletResponse response, SupplierDAO supplierDAO)
            throws ServletException, IOException {
        try {
            int supplierId = Integer.parseInt(request.getParameter("id"));
            Supplier existingSupplier = supplierDAO.getSupplierById(supplierId);

            if (existingSupplier == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }

            Supplier supplier = buildSupplierFromRequest(request, supplierId);
            String validationError = validateSupplier(supplier);

            if (validationError != null) {
                forwardManageSupplier(request, response, supplier, "update", validationError, true);
                return;
            }
            if (supplierDAO.existsByNameExceptId(supplier.getName(), supplierId)) {
                forwardManageSupplier(request, response, supplier, "update", "Supplier name already exists.", true);
                return;
            }

            boolean success = supplierDAO.updateSupplier(supplier);
            if (success) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=update");
                return;
            }

            forwardManageSupplier(request, response, supplier, "update", "Failed to update supplier.", true);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void forwardManageSupplier(HttpServletRequest request, HttpServletResponse response,
            Supplier selectedSupplier, String formMode, String error, boolean openModal)
            throws ServletException, IOException {
        SupplierDAO supplierDAO = new SupplierDAO();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("selectedSupplier", selectedSupplier);
        request.setAttribute("formMode", formMode);
        request.setAttribute("openModal", openModal);
        request.setAttribute("modalTitle", "update".equals(formMode) ? "Update Supplier" : "Add Supplier");
        request.setAttribute("submitLabel", "update".equals(formMode) ? "Update Changes" : "Add");
        request.setAttribute("actionValue", "update".equals(formMode) ? "update" : "add");
        request.setAttribute("currentView", "suppliers");
        request.setAttribute("contentPage", ADMIN_CONTENT);
        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }

        request.getRequestDispatcher(ADMIN_PANEL).forward(request, response);
    }

    private Supplier buildSupplierFromRequest(HttpServletRequest request, int supplierId) {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(supplierId);
        supplier.setName(trimToNull(request.getParameter("name")));
        supplier.setPhone(normalizePhone(request.getParameter("phone")));
        supplier.setAddress(trimToNull(request.getParameter("address")));
        supplier.setStatus(parseStatus(request.getParameter("status")));
        return supplier;
    }

    private String validateSupplier(Supplier supplier) {
        if (supplier.getName() == null) {
            return "Supplier name is required.";
        }
        if (supplier.getName().length() > 150) {
            return "Supplier name must not exceed 150 characters.";
        }
        if (supplier.getPhone() == null) {
            return "Phone is required.";
        }
        if (!supplier.getPhone().matches(PHONE_REGEX)) {
            return "Please enter a valid Vietnamese phone number.";
        }
        if (supplier.getAddress() == null) {
            return "Address is required.";
        }
        if (supplier.getAddress().length() > 255) {
            return "Address must not exceed 255 characters.";
        }
        return null;
    }

    private boolean parseStatus(String statusParam) {
        return "on".equalsIgnoreCase(statusParam)
                || "1".equals(statusParam)
                || "true".equalsIgnoreCase(statusParam);
    }

    private String normalizePhone(String value) {
        String phone = trimToNull(value);
        if (phone == null) {
            return null;
        }

        phone = phone.replaceAll("[\\s().-]", "");

        if (phone.startsWith("+84")) {
            return "0" + phone.substring(3);
        }
        if (phone.startsWith("84")) {
            return "0" + phone.substring(2);
        }
        return phone;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
