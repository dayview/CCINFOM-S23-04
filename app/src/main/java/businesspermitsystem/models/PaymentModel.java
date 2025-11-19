package businesspermitsystem.models;

import java.util.Date;

/**
 * Model class representing a payment record for a renewal application.
 */
public class PaymentModel {
    private int paymentId;
    private int renewalId;
    private double amount;
    private String paymentMethod;
    private Date paymentDate;

    /**
     * Constructor for creating a new payment.
     */
    public PaymentModel(int paymentId, int renewalId, double amount, String paymentMethod, Date paymentDate) {
        this.paymentId = paymentId;
        this.renewalId = renewalId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(int renewalId) {
        this.renewalId = renewalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}