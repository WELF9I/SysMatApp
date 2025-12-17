package com.institut.sysmat.desktop.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.institut.sysmat.desktop.config.AppConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    public ApiResponse executeRequest(String endpoint, HttpMethod method, Object requestBody) {
        return executeRequest(endpoint, method, requestBody, true);
    }
    
    public ApiResponse executeRequest(String endpoint, HttpMethod method, Object requestBody, boolean requireAuth) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = AppConfig.API_BASE_URL + endpoint;
        
        try {
            HttpRequestBase request = createRequest(url, method, requestBody);
            
            if (requireAuth && sessionManager.isLoggedIn()) {
                request.setHeader("Authorization", sessionManager.getAuthorizationHeader());
            }
            
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseString = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
                
                logger.debug("API Response - Status: {}, Endpoint: {}", statusCode, endpoint);
                
                if (statusCode >= 200 && statusCode < 300) {
                    if (responseString.isEmpty()) {
                        return new ApiResponse(true, "Opération réussie", null);
                    }
                    
                    JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();
                    boolean success = jsonResponse.get("success").getAsBoolean();
                    String message = jsonResponse.get("message").getAsString();
                    Object data = jsonResponse.has("data") ? jsonResponse.get("data") : null;
                    
                    return new ApiResponse(success, message, data);
                } else {
                    String errorMessage = extractErrorMessage(responseString);
                    return new ApiResponse(false, errorMessage, null);
                }
            }
            
        } catch (Exception e) {
            logger.error("Erreur API: {}", e.getMessage());
            return new ApiResponse(false, AppConfig.MSG_NETWORK_ERROR, null);
        }
    }
    
    private HttpRequestBase createRequest(String url, HttpMethod method, Object requestBody) {
        switch (method) {
            case GET:
                return new HttpGet(url);
            case POST:
                HttpPost post = new HttpPost(url);
                if (requestBody != null) {
                    post.setEntity(new StringEntity(gson.toJson(requestBody), StandardCharsets.UTF_8));
                }
                return post;
            case PUT:
                HttpPut put = new HttpPut(url);
                if (requestBody != null) {
                    put.setEntity(new StringEntity(gson.toJson(requestBody), StandardCharsets.UTF_8));
                }
                return put;
            case PATCH:
                HttpPatch patch = new HttpPatch(url);
                if (requestBody != null) {
                    patch.setEntity(new StringEntity(gson.toJson(requestBody), StandardCharsets.UTF_8));
                }
                return patch;
            case DELETE:
                return new HttpDelete(url);
            default:
                throw new IllegalArgumentException("Méthode HTTP non supportée: " + method);
        }
    }
    
    private String extractErrorMessage(String response) {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (json.has("message")) {
                return json.get("message").getAsString();
            }
            return "Erreur serveur";
        } catch (Exception e) {
            return "Erreur inconnue";
        }
    }
    
    public <T> T parseData(Object data, Class<T> type) {
        if (data == null) return null;
        return gson.fromJson(data.toString(), type);
    }
    
    // Classe pour les réponses API
    public static class ApiResponse {
        private final boolean success;
        private final String message;
        private final Object data;
        
        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
    
    // Enum pour les méthodes HTTP
    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE
    }
}