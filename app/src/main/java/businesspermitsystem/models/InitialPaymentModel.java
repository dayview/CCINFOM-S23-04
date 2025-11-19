package businesspermitsystem.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InitialPaymentModel {

    private int paymentId;
    private int applicationId;
    private int businessId;
    private int permitTypeId;
    private int municipalityId;

    private LocalDate paymentDate;
    private BigDecimal amountPaid;
    private String modeOfPayment;
    private String orNumber;

    // default contructor
    public InitialPaymentModel() {}


    public InitialPaymentModel(int paymentId, int applicationId, int businessId, int permitTypeId, int municipalityId, LocalDate paymentDate, BigDecimal amountPaid, String modeOfPayment, String orNumber) {

        this.paymentId = paymentId;
        this.applicationId = applicationId;
        this.businessId = businessId;
        this.permitTypeId = permitTypeId;
        this.municipalityId = municipalityId;
        this.paymentDate = paymentDate;
        this.amountPaid = amountPaid;
        this.modeOfPayment = modeOfPayment;
        this.orNumber = orNumber;
    }

    // Getters & Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public int getPermitTypeId() {
        return permitTypeId;
    }

    public void setPermitTypeId(int permitTypeId) {
        this.permitTypeId = permitTypeId;
    }

    public int getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public String getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(String orNumber) {
        this.orNumber = orNumber;
    }
}
