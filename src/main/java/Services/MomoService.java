package Services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

public class MomoService {

    private static final Logger LOGGER = Logger.getLogger(MomoService.class.getName());
    
    private static final String CONFIG_FILE = "momo.properties";
    
    private String endpoint;
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String redirectUrl;
    private String ipnUrl;

    public MomoService() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                LOGGER.warning("Unable to find " + CONFIG_FILE + ", using default config");
                setDefaultConfig();
                return;
            }
            props.load(input);
            
            this.endpoint = props.getProperty("momo.endpoint");
            this.partnerCode = props.getProperty("momo.partnerCode");
            this.accessKey = props.getProperty("momo.accessKey");
            this.secretKey = props.getProperty("momo.secretKey");
            this.redirectUrl = props.getProperty("momo.redirectUrl");
            this.ipnUrl = props.getProperty("momo.ipnUrl");
            
        } catch (IOException e) {
            LOGGER.warning("Error loading config, using default: " + e.getMessage());
            setDefaultConfig();
        }
    }

    private void setDefaultConfig() {
        this.endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        this.partnerCode = "MOMOLRJZ20181206";
        this.accessKey = "mTCKt9W3eU1m39TW";
        this.secretKey = "SetA5RDnLHvt51AULf51DyauxUo3kDU6";
        this.redirectUrl = "http://localhost:8080/averis_cosmetic_v1/momo-return";
        this.ipnUrl = "http://localhost:8080/averis_cosmetic_v1/momo-return";
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String createPayment(int orderId, long amount) {

        try {

            String requestId = String.valueOf(System.currentTimeMillis());

            String orderIdStr = "ORDER_" + orderId + "_" + requestId;

            String orderInfo = "Thanh toan don hang #" + orderId;

            String requestType = "payWithATM";

            String extraData = "";

            String amountStr = String.valueOf(amount);

            String rawSignature =
                    "accessKey=" + accessKey +
                    "&amount=" + amountStr +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderIdStr +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, secretKey);

            JSONObject json = new JSONObject();

            json.put("partnerCode", partnerCode);
            json.put("accessKey", accessKey);
            json.put("requestId", requestId);
            json.put("amount", amountStr);
            json.put("orderId", orderIdStr);
            json.put("orderInfo", orderInfo);
            json.put("redirectUrl", redirectUrl);
            json.put("ipnUrl", ipnUrl);
            json.put("extraData", extraData);
            json.put("requestType", requestType);
            json.put("signature", signature);
            json.put("lang", "vi");

            URL url = new URL(endpoint);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();

            os.write(json.toString().getBytes("UTF-8"));

            os.flush();

            os.close();

            int status = conn.getResponseCode();

            InputStream is;

            if (status >= 400) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            String response;
            try (Scanner scanner = new Scanner(is).useDelimiter("\\A")) {
                response = scanner.hasNext() ? scanner.next() : "";
            }

            LOGGER.info("Momo response: " + response);

            JSONObject responseJson = new JSONObject(response);

            if (responseJson.has("payUrl")) {
                return responseJson.getString("payUrl");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating MoMo payment", e);
        }

        return null;
    }

    public boolean verifyCallback(Properties params) {
        try {
            String receivedSignature = params.getProperty("signature");
            if (receivedSignature == null || receivedSignature.isEmpty()) {
                LOGGER.warning("No signature in MoMo callback");
                return false;
            }

            LOGGER.info("=== MoMo Callback Parameters ===");
            for (String key : params.stringPropertyNames()) {
                LOGGER.info(key + " = " + params.getProperty(key));
            }

            String[] paramOrder = {"partnerCode", "orderId", "requestId", "amount", "orderInfo", 
                                   "orderType", "transId", "resultCode", "message", "payType", 
                                   "extraData", "responseTime"};
            
            StringBuilder rawSignatureBuilder = new StringBuilder();
            boolean first = true;
            for (String key : paramOrder) {
                String value = params.getProperty(key);
                if (value != null && !value.isEmpty()) {
                    if (!first) {
                        rawSignatureBuilder.append("&");
                    }
                    rawSignatureBuilder.append(key).append("=").append(value);
                    first = false;
                }
            }

            String rawSignature = rawSignatureBuilder.toString();

            String expectedSignature = hmacSHA256(rawSignature, accessKey);

            LOGGER.info("Raw signature string: " + rawSignature);
            LOGGER.info("Received signature: " + receivedSignature);
            LOGGER.info("Expected signature (with accessKey): " + expectedSignature);

            String expectedSignatureSecret = hmacSHA256(rawSignature, secretKey);
            LOGGER.info("Expected signature (with secretKey): " + expectedSignatureSecret);

            return receivedSignature.equals(expectedSignature);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying MoMo callback", e);
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");

        mac.init(secretKeySpec);

        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {

            String s = Integer.toHexString(0xff & b);

            if (s.length() == 1) {
                hex.append('0');
            }

            hex.append(s);
        }

        return hex.toString();
    }
}
