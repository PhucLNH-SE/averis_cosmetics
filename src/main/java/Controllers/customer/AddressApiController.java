package Controllers.customer;

import Utils.VietnamAddressApiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AddressApiController extends HttpServlet {

    private final VietnamAddressApiService addressApiService = new VietnamAddressApiService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "provinces";
        }

        try {
            String data;

            switch (action) {
                case "provinces":
                    data = addressApiService.getProvincesJson();
                    break;
                case "wards":
                    data = addressApiService.getWardsByProvinceJson(requireParam(request, "provinceCode"));
                    break;
                case "legacyDistricts":
                    data = addressApiService.getLegacyWardsByWardJson(requireParam(request, "wardCode"));
                    break;
                default:
                    writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }

            writeJson(response, HttpServletResponse.SC_OK, data);
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            writeError(response, HttpServletResponse.SC_BAD_GATEWAY, "Address API request was interrupted");
        } catch (IOException ex) {
            writeError(response, HttpServletResponse.SC_BAD_GATEWAY, "Unable to load latest Vietnam address data");
        }
    }

    private String requireParam(HttpServletRequest request, String key) {
        String value = request.getParameter(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        return value.trim();
    }

    private void writeJson(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body == null ? "{}" : body);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        String safeMessage = escapeJson(message == null ? "Unknown error" : message);
        writeJson(response, status, "{\"error\":\"" + safeMessage + "\"}");
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", " ");
    }
}
