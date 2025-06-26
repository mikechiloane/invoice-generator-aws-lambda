package com.recceda.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recceda.invoice.common.CustomerInvoiceData;
import com.recceda.invoice.common.InvoiceDates;
import com.recceda.invoice.common.InvoiceItem;
import com.recceda.invoice.common.TotalsAndTaxInfo;

/**
 * Builder class for creating CustomerInvoiceData objects from JSON or
 * individual components
 */
public class CustomerInvoiceDataBuilder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse JSON string and build CustomerInvoiceData object
     * 
     * @param jsonString JSON string containing invoice data
     * @return CustomerInvoiceData object
     * @throws IOException if JSON parsing fails
     */
    public static CustomerInvoiceData fromJson(String jsonString) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonString);

        String customerName = rootNode.get("customerName").asText();

        JsonNode addressLinesNode = rootNode.get("addressLines");
        String[] addressLines = parseAddressLines(addressLinesNode);

        JsonNode invoiceDatesNode = rootNode.get("invoiceDates");
        InvoiceDates invoiceDates = parseInvoiceDates(invoiceDatesNode);

        JsonNode itemsNode = rootNode.get("items");

        InvoiceItem[] items = parseInvoiceItems(itemsNode);

        JsonNode totalsNode = rootNode.get("totalsAndTaxInfo");
        TotalsAndTaxInfo totalsAndTaxInfo = parseTotalsAndTaxInfo(totalsNode);

        return new CustomerInvoiceData(customerName, addressLines, items, invoiceDates, totalsAndTaxInfo);
    }

    /**
     * Parse JSON byte array and build CustomerInvoiceData object
     * 
     * @param jsonBytes JSON byte array containing invoice data
     * @return CustomerInvoiceData object
     * @throws IOException if JSON parsing fails
     */
    public static CustomerInvoiceData fromJson(byte[] jsonBytes) throws IOException {
        return fromJson(new String(jsonBytes));
    }

    /**
     * Build CustomerInvoiceData from individual components
     */
    public static CustomerInvoiceData build(String customerName, String[] addressLines,
            InvoiceItem[] items, InvoiceDates invoiceDates,
            TotalsAndTaxInfo totalsAndTaxInfo) {
        return new CustomerInvoiceData(customerName, addressLines, items, invoiceDates, totalsAndTaxInfo);
    }

    private static String[] parseAddressLines(JsonNode addressLinesNode) {
        List<String> addressList = new ArrayList<>();
        if (addressLinesNode.isArray()) {
            for (JsonNode line : addressLinesNode) {
                addressList.add(line.asText());
            }
        }
        return addressList.toArray(String[]::new);
    }

    private static InvoiceDates parseInvoiceDates(JsonNode invoiceDatesNode) {
        String invoiceDate = invoiceDatesNode.get("invoiceDate").asText();
        String invoiceDueByDate = invoiceDatesNode.get("invoiceDueByDate").asText();
        return new InvoiceDates(invoiceDate, invoiceDueByDate);
    }

    private static InvoiceItem[] parseInvoiceItems(JsonNode itemsNode) {
        List<InvoiceItem> itemsList = new ArrayList<>();
        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                Integer quantity = itemNode.get("quantity").asInt();
                Double unitPrice = itemNode.get("unitPrice").asDouble();
                String itemId = itemNode.has("itemId") ? itemNode.get("itemId").asText() : "";
                String description = itemNode.get("description").asText();

                InvoiceItem item = new InvoiceItem(quantity, unitPrice, itemId, description);
                itemsList.add(item);
            }
        }
        return itemsList.toArray(InvoiceItem[]::new);
    }

    private static TotalsAndTaxInfo parseTotalsAndTaxInfo(JsonNode totalsNode) {
        String subTotal = totalsNode.get("subTotal").asText();
        String tax = totalsNode.get("tax").asText();
        String taxRate = totalsNode.get("taxRate").asText();
        String total = totalsNode.get("total").asText();
        return new TotalsAndTaxInfo(subTotal, tax, taxRate, total);
    }
}
