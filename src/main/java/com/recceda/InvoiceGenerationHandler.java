package com.recceda;

import java.io.File;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.recceda.builder.CustomerInvoiceDataBuilder;
import com.recceda.invoice.api.InvoiceGenerator;
import com.recceda.invoice.common.CustomerInvoiceData;

public class InvoiceGenerationHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context rContext) {
        try {
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(200);

            configureResponseHeaders(responseEvent);
            responseEvent.setBody("Invoice generated successfully");

            CustomerInvoiceData customerInvoiceData = CustomerInvoiceDataBuilder.fromJson(requestEvent.getBody());
            File invoiceFile = new InvoiceGenerator().generateInvoice(customerInvoiceData);
            if (invoiceFile != null && invoiceFile.exists()) {
                responseEvent.setBody("Invoice generated successfully: " + invoiceFile.getAbsolutePath());
            } else {
                responseEvent.setStatusCode(500);
                responseEvent.setBody("Failed to generate invoice");
            }
            return responseEvent;

        } catch (Exception e) {
            APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
            errorResponse.setStatusCode(500);
            errorResponse.setHeaders(Map.of(
                    "Content-Type", "application/json",
                    "Access-Control-Allow-Origin", "*",
                    "Access-Control-Allow-Headers", "*",
                    "Access-Control-Allow-Methods", "GET,POST,OPTIONS"));
            errorResponse.setBody("{\"error\": \"" + e.getMessage() + "\"}");
            return errorResponse;
        }

    }

    private void configureResponseHeaders(APIGatewayProxyResponseEvent responseEvent) {
        responseEvent.setHeaders(Map.of(
                "Content-Type", "application/pdf",
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Headers", "*",
                "Access-Control-Allow-Methods", "GET,POST,OPTIONS"));
    }

}
