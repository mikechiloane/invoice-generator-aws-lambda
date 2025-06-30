package com.recceda.mapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.recceda.builder.CustomerInvoiceDataBuilder;
import com.recceda.invoice.api.InvoiceGenerator;
import com.recceda.invoice.common.CustomerInvoiceData;
import com.recceda.invoice.common.InvoiceDates;
import com.recceda.invoice.common.InvoiceItem;
import com.recceda.invoice.common.TotalsAndTaxInfo;

class CustomerInvoiceDataBuilderTest {

    @Test
    void testFromJsonString() throws IOException {
        String jsonString = "{" +
                "\"customerName\": \"John Doe\"," +
                "\"addressLines\": [\"123 Main St\", \"Apt 4B\"]," +
                "\"invoiceDates\": {\"invoiceDate\": \"2025-06-01\", \"invoiceDueByDate\": \"2025-06-15\"}," +
                "\"items\": [{\"itemName\": \"Widget\", \"quantity\": 10, \"unitPrice\": 2.5}],"
                +
                "\"totalsAndTaxInfo\": {\"subTotal\": \"25.00\", \"tax\": \"2.50\", \"taxRate\": \"10%\", \"total\": \"27.50\"}"
                +
                "}";

        CustomerInvoiceData invoiceData = CustomerInvoiceDataBuilder.fromJson(jsonString);

        assertEquals("John Doe", invoiceData.getCustomerName());
        assertArrayEquals(new String[] { "123 Main St", "Apt 4B" }, invoiceData.getAddressLines());
        assertEquals("2025-06-01", invoiceData.getInvoiceDates().getInvoiceDate());
        assertEquals("2025-06-15", invoiceData.getInvoiceDates().getInvoiceDueByDate());

        InvoiceItem[] items = invoiceData.getItems();
        assertEquals(1, items.length);
        assertEquals(10, items[0].getQuantity());
        assertEquals(2.5, items[0].getUnitPrice());

        TotalsAndTaxInfo totals = invoiceData.getTotalsAndTaxInfo();
        assertEquals("25.00", totals.getSubTotal());
        assertEquals("2.50", totals.getTax());
        assertEquals("10%", totals.getTaxRate());
        assertEquals("27.50", totals.getTotal());

    }

    @Test
    void testFromJsonBytes() throws IOException {
        String jsonString = "{" +
                "\"customerName\": \"Jane Smith\"," +
                "\"addressLines\": [\"456 Elm St\", \"Suite 300\"]," +
                "\"invoiceDates\": {\"invoiceDate\": \"2025-06-10\", \"invoiceDueByDate\": \"2025-06-20\"}," +
                "\"items\": [{ \"quantity\": 5, \"unitPrice\": 15.0, \"itemId\": \"G456\", \"description\": \"An advanced gadget\"}],"
                +
                "\"totalsAndTaxInfo\": {\"subTotal\": \"75.00\", \"tax\": \"7.50\", \"taxRate\": \"10%\", \"total\": \"82.50\"}"
                +
                "}";

        byte[] jsonBytes = jsonString.getBytes();

        CustomerInvoiceData invoiceData = CustomerInvoiceDataBuilder.fromJson(jsonBytes);

        assertEquals("Jane Smith", invoiceData.getCustomerName());
        assertArrayEquals(new String[] { "456 Elm St", "Suite 300" }, invoiceData.getAddressLines());
        assertEquals("2025-06-10", invoiceData.getInvoiceDates().getInvoiceDate());
        assertEquals("2025-06-20", invoiceData.getInvoiceDates().getInvoiceDueByDate());

        InvoiceItem[] items = invoiceData.getItems();
        assertEquals(1, items.length);
        assertEquals(5, items[0].getQuantity());
        assertEquals(15.0, items[0].getUnitPrice());

        TotalsAndTaxInfo totals = invoiceData.getTotalsAndTaxInfo();
        assertEquals("75.00", totals.getSubTotal());
        assertEquals("7.50", totals.getTax());
        assertEquals("10%", totals.getTaxRate());
        assertEquals("82.50", totals.getTotal());
    }

    @Test
    void testBuild() {
        String customerName = "Alice Johnson";
        String[] addressLines = { "789 Oak St", "Floor 2" };
        InvoiceItem[] items = { new InvoiceItem(3, 20.0,"A versatile thingamajig") };
        InvoiceDates invoiceDates = new InvoiceDates("2025-06-15", "2025-06-30");
        TotalsAndTaxInfo totalsAndTaxInfo = new TotalsAndTaxInfo("60.00", "6.00", "10%", "66.00");

        CustomerInvoiceData invoiceData = CustomerInvoiceDataBuilder.build(customerName, addressLines, items,
                invoiceDates, totalsAndTaxInfo);

        assertEquals("Alice Johnson", invoiceData.getCustomerName());
        assertArrayEquals(new String[] { "789 Oak St", "Floor 2" }, invoiceData.getAddressLines());
        assertEquals("2025-06-15", invoiceData.getInvoiceDates().getInvoiceDate());
        assertEquals("2025-06-30", invoiceData.getInvoiceDates().getInvoiceDueByDate());

        InvoiceItem[] itemsResult = invoiceData.getItems();
        assertEquals(1, itemsResult.length);
        assertEquals(3, itemsResult[0].getQuantity());
        assertEquals(20.0, itemsResult[0].getUnitPrice());

        TotalsAndTaxInfo totalsResult = invoiceData.getTotalsAndTaxInfo();
        assertEquals("60.00", totalsResult.getSubTotal());
        assertEquals("6.00", totalsResult.getTax());
        assertEquals("10%", totalsResult.getTaxRate());
        assertEquals("66.00", totalsResult.getTotal());

        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
        invoiceGenerator.generateInvoiceToPath(invoiceData, "invoice.pdf");

    }
}
