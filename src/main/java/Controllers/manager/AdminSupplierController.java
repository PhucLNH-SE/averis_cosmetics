package Controllers.manager;

import DALs.SupplierDAO;
import Model.Supplier;
import Utils.ValidationUtil;
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

    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            action = "list";
        }

        switch (action) {
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

        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
            return;
        }

        switch (action) {
            case "add":
                addSupplier(request, response);
                break;
            case "update":
                updateSupplier(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL);
                break;
        }
    }

    private void showSupplierList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardManageSupplier(request, response, null, null, null);
    }

    private void showEditSupplierForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = trimToNull(request.getParameter("id"));
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int supplierId = Integer.parseInt(idParam);
            Supplier selectedSupplier = supplierDAO.getSupplierById(supplierId);
            if (selectedSupplier == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }
            forwardManageSupplier(request, response, selectedSupplier, "update", null);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
        }
    }

    private void addSupplier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean status = parseStatus(request.getParameter("status"));
        Supplier selectedSupplier = ValidationUtil.normalizeSupplier(
                0,
                request.getParameter("name"),
                request.getParameter("phone"),
                request.getParameter("address"),
                status);
        String name = selectedSupplier.getName();

        try {
            ValidationUtil.validateSupplierInput(selectedSupplier);
        } catch (IllegalArgumentException ex) {
            forwardManageSupplier(request, response, selectedSupplier, "add", ex.getMessage());
            return;
        }
        if (supplierDAO.existsByName(name)) {
            forwardManageSupplier(request, response, selectedSupplier, "add", "Supplier name already exists.");
            return;
        }

        boolean added = supplierDAO.insertSupplier(selectedSupplier);
        if (added) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=add");
            return;
        }

        forwardManageSupplier(request, response, selectedSupplier, "add", "Failed to add supplier.");
    }

    private void updateSupplier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = trimToNull(request.getParameter("id"));
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
            return;
        }

        try {
            int supplierId = Integer.parseInt(idParam);
            Supplier existingSupplier = supplierDAO.getSupplierById(supplierId);
            if (existingSupplier == null) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=notFound");
                return;
            }

            boolean status = parseStatus(request.getParameter("status"));
            Supplier selectedSupplier = ValidationUtil.normalizeSupplier(
                    supplierId,
                    request.getParameter("name"),
                    request.getParameter("phone"),
                    request.getParameter("address"),
                    status);
            String name = selectedSupplier.getName();

            try {
                ValidationUtil.validateSupplierInput(selectedSupplier);
            } catch (IllegalArgumentException ex) {
                forwardManageSupplier(request, response, selectedSupplier, "update", ex.getMessage());
                return;
            }
            if (supplierDAO.existsByNameExceptId(name, supplierId)) {
                forwardManageSupplier(request, response, selectedSupplier, "update", "Supplier name already exists.");
                return;
            }

            boolean updated = supplierDAO.updateSupplier(selectedSupplier);
            if (updated) {
                response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?success=update");
                return;
            }

            forwardManageSupplier(request, response, selectedSupplier, "update", "Failed to update supplier.");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ADMIN_LIST_URL + "?error=updateFailed");
        }
    }

    private void forwardManageSupplier(HttpServletRequest request, HttpServletResponse response,
            Supplier selectedSupplier, String formMode, String error)
            throws ServletException, IOException {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("selectedSupplier", selectedSupplier);
        request.setAttribute("formMode", formMode);
        if (error != null && !error.trim().isEmpty()) {
            request.setAttribute("error", error);
        }
        request.setAttribute("currentView", "suppliers");
        request.setAttribute("contentPage", ADMIN_CONTENT);
        request.getRequestDispatcher(ADMIN_PANEL).forward(request, response);
    }

    private boolean parseStatus(String statusParam) {
        return "on".equalsIgnoreCase(statusParam)
                || "1".equals(statusParam)
                || "true".equalsIgnoreCase(statusParam);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
