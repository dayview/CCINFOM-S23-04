package businesspermitsystem.models;

import java.math.BigDecimal;

public class PermitTypeModel {

    private int permitTypeId;
    private String permitName;
    private BigDecimal baseFee;
    private String surchargeRule;
    private int validityMonths;
    private String documentRequirements;

    public PermitTypeModel(int permitTypeId, String permitName, BigDecimal baseFee,
                           String surchargeRule, int validityMonths, String documentRequirements) {
        this.permitTypeId = permitTypeId;
        this.permitName = permitName;
        this.baseFee = baseFee;
        this.surchargeRule = surchargeRule;
        this.validityMonths = validityMonths;
        this.documentRequirements = documentRequirements;
    }

    // Getters
    public int getPermitTypeId() {
        return permitTypeId;
    }

    public String getPermitName() {
        return permitName;
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public String getSurchargeRule() {
        return surchargeRule;
    }

    public int getValidityMonths() {
        return validityMonths;
    }

    public String getDocumentRequirements() {
        return documentRequirements;
    }

    // Setters
    public void setPermitTypeId(int permitTypeId) {
        this.permitTypeId = permitTypeId;
    }

    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public void setSurchargeRule(String surchargeRule) {
        this.surchargeRule = surchargeRule;
    }

    public void setValidityMonths(int validityMonths) {
        this.validityMonths = validityMonths;
    }

    public void setDocumentRequirements(String documentRequirements) {
        this.documentRequirements = documentRequirements;
    }

    @Override
    public String toString() {
        return permitName;
    }
}
