package Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class VietnamAddressApiService {

    private static final String BASE_URL = "https://provinces.open-api.vn/api/v2";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    public String getProvincesJson() throws IOException, InterruptedException {
        return sendGet(BASE_URL + "/?depth=1");
    }

    public String getWardsByProvinceJson(String provinceCode) throws IOException, InterruptedException {
        String encodedProvinceCode = URLEncoder.encode(provinceCode, StandardCharsets.UTF_8);
        return sendGet(BASE_URL + "/p/" + encodedProvinceCode + "?depth=2");
    }

    public String getLegacyWardsByWardJson(String wardCode) throws IOException, InterruptedException {
        String encodedWardCode = URLEncoder.encode(wardCode, StandardCharsets.UTF_8);
        return sendGet(BASE_URL + "/w/" + encodedWardCode + "/to-legacies/");
    }

    private String sendGet(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .header("User-Agent", "averis-cosmetics-address-api")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        int statusCode = response.statusCode();

        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Address API returned HTTP status: " + statusCode);
        }

        return response.body();
    }
}
