package businesspermitsystem.models;

import java.util.Date;

public class PaymentModel {
    private int paymentID;
    private int renewalID;
    private double amount;
    private String method;
    private Date paymentDate;

    public PaymentModel(int paymentID, int renewalID, double amount, String method, Date paymentDate) {
        this.paymentID = paymentID;
        this.renewalID = renewalID;
        this.amount = amount;
        this.method = method;
        this.paymentDate = paymentDate;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getRenewalID() {
        return renewalID;
    }

    public void setRenewalID(int renewalID) {
        this.renewalID = renewalID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
