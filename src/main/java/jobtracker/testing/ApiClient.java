package jobtracker.testing;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * HTTP client for testing REST API endpoints
 * Handles all HTTP requests (GET, POST, PUT, DELETE) with JSON support
 */
public class ApiClient {
    private final HttpClient httpClient;
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * GET request to an endpoint
     */
    public ApiResponse get(String path) {
        try {
            HttpGet request = new HttpGet(baseUrl + path);
            request.setHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(request);
            return new ApiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        }
    }

    /**
     * POST request with JSON body
     */
    public ApiResponse post(String path, String jsonBody) {
        try {
            HttpPost request = new HttpPost(baseUrl + path);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonBody));
            HttpResponse response = httpClient.execute(request);
            return new ApiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("POST request failed: " + e.getMessage(), e);
        }
    }

    /**
     * PUT request with JSON body
     */
    public ApiResponse put(String path, String jsonBody) {
        try {
            HttpPut request = new HttpPut(baseUrl + path);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonBody));
            HttpResponse response = httpClient.execute(request);
            return new ApiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("PUT request failed: " + e.getMessage(), e);
        }
    }

    /**
     * DELETE request
     */
    public ApiResponse delete(String path) {
        try {
            HttpDelete request = new HttpDelete(baseUrl + path);
            request.setHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(request);
            return new ApiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("DELETE request failed: " + e.getMessage(), e);
        }
    }

    /**
     * API Response wrapper
     */
    public static class ApiResponse {
        private final int statusCode;
        private final String body;
        private final JsonElement bodyJson;

        public ApiResponse(HttpResponse response) throws Exception {
            this.statusCode = response.getStatusLine().getStatusCode();
            this.body = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
            try {
                this.bodyJson = body.isEmpty() ? null : JsonParser.parseString(body);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JSON response: " + body, e);
            }
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public JsonElement getJson() {
            return bodyJson;
        }

        public String getString() {
            return body;
        }

        public String getAsString() {
            if (bodyJson == null) return null;
            if (bodyJson.isJsonPrimitive()) {
                return bodyJson.getAsString();
            }
            // For non-primitive responses, return the raw body with surrounding quotes stripped
            String raw = body.trim();
            if (raw.startsWith("\"") && raw.endsWith("\"")) {
                return raw.substring(1, raw.length() - 1);
            }
            return raw;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isError() {
            return statusCode >= 400;
        }
    }
}
