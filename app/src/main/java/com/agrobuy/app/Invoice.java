package com.agrobuy.app;

public class Invoice {
    String invoiceNumber;
    String customerName;
    String invoiceAmount;
    String invoiceDueDate;
    String paymentTerms;
    String deliveryMode;
    String deliveryDestination;
    String invoiceURL;

    public Invoice(String invoiceNumber, String customerName, String invoiceAmount, String invoiceDueDate
            , String paymentTerms, String deliveryMode, String deliveryDestination) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.invoiceAmount = invoiceAmount;
        this.invoiceDueDate = invoiceDueDate;
        this.paymentTerms = paymentTerms;
        this.deliveryMode = deliveryMode;
        this.deliveryDestination = deliveryDestination;
    }

    public Invoice(String invoiceNumber, String invoiceURL) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceURL = invoiceURL;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public String getInvoiceDueDate() {
        return invoiceDueDate;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getDeliveryDestination() {
        return deliveryDestination;
    }

    public String getInvoiceURL() {
        return invoiceURL;
    }
}
