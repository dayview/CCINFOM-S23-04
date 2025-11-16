package businesspermitsystem.models;

import java.util.Date;

public class PermitRenewalApplicationModel {
    private int renewalID;
    private int businessID;
    private int permitID;
    private Date applicationDate;
    private double renewalFee;
    private double surcharge;
    private double totalAmount;
    private String status;

    public PermitRenewalApplicationModel(int renewalID, int businessID, int permitID, Date applicationDate, double renewalFee,
            double surcharge, double totalAmount, String status) {
                this.renewalID = renewalID;
                this.businessID = businessID;
                this.permitID = permitID;
                this.applicationDate = applicationDate;
                this.renewalFee = renewalFee;
                this.surcharge = surcharge;
                this.totalAmount = totalAmount;
                this.status = status;
    }
    
    public int getRenewalID() {
        return renewalID;
    }

    public void setRenewalID(int renewalID) {
        this.renewalID = renewalID;
    }

    public int getBusinessID() {
        return businessID;
    }

    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    public int getPermitID() {
        return permitID;
    }

    public void setPermitID(int permitID) {
        this.permitID = permitID;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public double getRenewalFee() {
        return renewalFee;
    }

    public void setRenewalFee(double renewalFee) {
        this.renewalFee = renewalFee;
    }

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
