package com.recceda;

import java.io.File;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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
        LambdaLogger logger = rContext.getLogger();

        try {

            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(200);

            CustomerInvoiceData customerInvoiceData = CustomerInvoiceDataBuilder.fromJson(requestEvent.getBody());
            String pdfFileName = "/tmp/invoice-" + UUID.randomUUID() + ".pdf";
            File invoiceFile = new InvoiceGenerator().generateInvoiceToPath(customerInvoiceData, pdfFileName);
            if (invoiceFile != null && invoiceFile.exists()) {
                configureResponseHeaders(responseEvent);
                responseEvent.setIsBase64Encoded(true);
                responseEvent.setBody(
                        Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(invoiceFile.toPath())));
                
                invoiceFile.delete();
            } else {
                responseEvent.setStatusCode(500);
                configureResponseHeaders(responseEvent);
                responseEvent.setHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Access-Control-Allow-Origin", "*",
                        "Access-Control-Allow-Headers", "*",
                        "Access-Control-Allow-Methods", "GET,POST,OPTIONS"));
                responseEvent.setBody("{\"error\": \"Failed to generate invoice\"}");
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
            logger.log("Error generating invoice: " + e);
            return errorResponse;
        }
        

    }

private void configureResponseHeaders(APIGatewayProxyResponseEvent responseEvent) {
    responseEvent.setHeaders(Map.of(
            "Content-Type", "application/pdf",
            "Content-Disposition", "inline; filename=\"invoice.pdf\"",
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "Content-Type, Authorization",
            "Access-Control-Allow-Methods", "GET,POST,OPTIONS",
            "Cache-Control", "no-cache"));
}

}
