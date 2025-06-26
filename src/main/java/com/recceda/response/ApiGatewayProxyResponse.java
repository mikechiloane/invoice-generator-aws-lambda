package com.recceda.response;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiGatewayProxyResponse {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final int statusCode;
    private final Map<String, String> headers = Map.of(
            "Content-Type", "application/pdf",
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "*",
            "Access-Control-Allow-Methods", "GET,POST,OPTIONS"
    );
  

    public ApiGatewayProxyResponse(int statusCode, String body) {
        this.statusCode = statusCode;
    }



    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


}