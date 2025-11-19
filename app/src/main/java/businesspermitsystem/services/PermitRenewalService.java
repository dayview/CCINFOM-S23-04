package businesspermitsystem.services;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;

import java.util.Calendar;
import java.util.Date; 
import java.util.List;

/**
 * Service class for handling permit renewal workflow.
 * Manages the complete renewal process: application, payment, inspection scheduling, and finalization.
 */
public class PermitRenewalService {
    
    private PermitRenewalApplicationDAO renewalDAO;
    private PaymentDAO paymentDAO;
    private InspectionDAO inspectionDAO;
    private InspectionScheduleDAO scheduleDAO;
    private InspectionResultDAO resultDAO;
    private BusinessDAO businessDAO;
    private PermitDAO permitDAO;
    private PermitTypeDAO typeDAO;
    
    public PermitRenewalService() {
        this.renewalDAO = new PermitRenewalApplicationDAO();
        this.paymentDAO = new PaymentDAO();
        this.inspectionDAO = new InspectionDAO();
        this.scheduleDAO = new InspectionScheduleDAO();
        this.resultDAO = new InspectionResultDAO();
        this.businessDAO = new BusinessDAO();
        this.permitDAO = new PermitDAO();
        this.typeDAO = new PermitTypeDAO();
    }
    
    /**
     * STEP 1: Creates a renewal application for an existing permit.
     * Validates business status, permit existence, and calculates fees including surcharges.
     * 
     * @param businessId the ID of the business applying for renewal
     * @param permitId the ID of the permit to be renewed
     * @return the renewal application ID if successful
     * @throws Exception if business is not active, permit not found, or validation fails
     */
    public int applyForRenewal(int businessId, int permitId) throws Exception {
        // Validate business
        BusinessModel business = businessDAO.getBusinessByID(businessId);
        if (business == null || !"Active".equals(business.getStatus())) {
            throw new Exception("Business not active or not found");
        }
        
        // Validate permit
        PermitModel permit = permitDAO.getPermitByID(permitId);
        if (permit == null) {
            throw new Exception("Permit not found");
        }
        
        // Verify permit belongs to the business
        if (permit.getBusinessID() != businessId) {
            throw new Exception("Permit does not belong to this business");
        }
        
        // Get permit type for fee calculation
        PermitTypeModel type = typeDAO.getPermitTypeByID(permit.getPermitTypeID());
        if (type == null) {
            throw new Exception("Permit type not found");
        }
        
        // Calculate fees
        double baseFee = type.getFeeSchedule().getBaseFee();
        double surcharge = calculateSurcharge(permit, type);
        double total = baseFee + surcharge;
        
        // Create renewal application
        PermitRenewalApplicationModel renewal = new PermitRenewalApplicationModel(
            0, businessId, permitId, new Date(), baseFee, surcharge, total, "pending"
        );
        
        int renewalId = renewalDAO.addRenewalGetID(renewal);
        if (renewalId <= 0) {
            throw new Exception("Failed to create renewal application");
        }
        
        return renewalId;
    }
    
    /**
     * STEP 2: Records a payment for a renewal application.
     * Updates the renewal status to 'paid' after successful payment.
     * 
     * @param renewalId the renewal application ID
     * @param amount the payment amount
     * @param method the payment method (Cash, GCash, etc.)
     * @return true if payment recorded successfully
     * @throws Exception if renewal not found or amount is insufficient
     */
    public boolean recordPayment(int renewalId, double amount, String method) throws Exception {
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            throw new Exception("Renewal application not found");
        }
        
        if (!"pending".equals(renewal.getStatus())) {
            throw new Exception("Renewal application is not in pending status");
        }
        
        if (amount < renewal.getTotalAmount()) {
            throw new Exception(String.format(
                "Payment amount (₱%.2f) is less than total amount due (₱%.2f)", 
                amount, renewal.getTotalAmount()
            ));
        }
        
        // Record payment
        PaymentModel payment = new PaymentModel(0, renewalId, amount, method, new Date());
        int paymentId = paymentDAO.addPaymentGetID(payment);
        
        if (paymentId <= 0) {
            throw new Exception("Failed to record payment");
        }
        
        // Update renewal status
        renewal.setStatus("paid");
        boolean updated = renewalDAO.updateRenewalApplication(renewal);
        
        if (!updated) {
            throw new Exception("Failed to update renewal status after payment");
        }
        
        return true;
    }
    
    /**
     * STEP 3: Schedules an inspection for a paid renewal application.
     * Creates both inspection and inspection schedule records.
     * 
     * @param renewalId the renewal application ID
     * @param inspectorId the inspector assigned to the inspection
     * @param date the scheduled inspection date
     * @return true if inspection scheduled successfully
     * @throws Exception if renewal not found or payment not completed
     */
    public boolean scheduleInspection(int renewalId, int inspectorId, Date date) throws Exception {
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            throw new Exception("Renewal application not found");
        }
        
        if (!"paid".equals(renewal.getStatus())) {
            throw new Exception("Payment must be completed before scheduling inspection");
        }
        
        // Check if inspection already scheduled
        if (isInspectionScheduled(renewalId)) {
            throw new Exception("Inspection already scheduled for this renewal");
        }
        
        // Create inspection record
        InspectionModel inspection = new InspectionModel(0, renewalId, inspectorId, date);
        int inspectionId = inspectionDAO.addInspectionGetID(inspection);
        
        if (inspectionId <= 0) {
            throw new Exception("Failed to create inspection record");
        }
        
        // Create inspection schedule
        InspectionScheduleModel schedule = new InspectionScheduleModel();
        schedule.setBusinessID(renewal.getBusinessID());
        schedule.setInspectorID(inspectorId);
        schedule.setInspectionDate(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        schedule.setStatus("Scheduled");
        
        boolean scheduled = scheduleDAO.addSchedule(schedule);
        
        if (!scheduled) {
            throw new Exception("Failed to create inspection schedule");
        }
        
        return true;
    }
    
    /**
     * STEP 4: Finalizes a renewal application after inspection.
     * Updates permit status based on inspection result (PASS or FAIL).
     * For passed inspections: renews the permit and extends validity.
     * For failed inspections: denies renewal and suspends the permit.
     * 
     * @param renewalId the renewal application ID
     * @param scheduleId the inspection schedule ID
     * @param result the inspection result (PASS or FAIL)
     * @param remarks additional comments about the inspection
     * @return true if finalization successful
     * @throws Exception if validation fails or database updates fail
     */
    public boolean finalizeRenewal(int renewalId, int scheduleId, String result, String remarks) throws Exception {
        // Validate result
        if (result == null || (!result.equalsIgnoreCase("PASS") && !result.equalsIgnoreCase("FAIL"))) {
            throw new Exception("Invalid inspection result. Must be PASS or FAIL");
        }
        
        // Get renewal application
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            throw new Exception("Renewal application not found");
        }
        
        if (!"paid".equals(renewal.getStatus())) {
            throw new Exception("Renewal must be in paid status before finalization");
        }
        
        // Get permit to be renewed
        PermitModel permit = permitDAO.getPermitByID(renewal.getPreviousPermitID());
        if (permit == null) {
            throw new Exception("Original permit not found");
        }
        
        // Get permit type for validity calculation
        PermitTypeModel type = typeDAO.getPermitTypeByID(permit.getPermitTypeID());
        if (type == null) {
            throw new Exception("Permit type not found");
        }
        
        // Record inspection result
        InspectionResultModel inspectionResult = new InspectionResultModel();
        inspectionResult.setScheduleId(scheduleId);
        inspectionResult.setResult(result);
        inspectionResult.setRemarks(remarks);
        
        if (!resultDAO.addResult(inspectionResult)) {
            throw new Exception("Failed to record inspection result");
        }
        
        // Update inspection schedule status
        InspectionScheduleModel schedule = scheduleDAO.getScheduleByID(scheduleId);
        if (schedule != null) {
            schedule.setStatus("Completed");
            scheduleDAO.updateSchedule(schedule);
        }
        
        Date now = new Date();
        
        if ("PASS".equalsIgnoreCase(result)) {
            // Approve renewal
            renewal.setStatus("approved");
            
            // Calculate new validity period
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            Date newValidityStart = now;
            cal.add(Calendar.MONTH, type.getValidityMonths());
            Date newValidityEnd = cal.getTime();
            
            // Update permit with renewed status
            permit.setStatus("renewed");
            permit.setValidityStart(newValidityStart);
            permit.setValidityEnd(newValidityEnd);
            permit.setStatusEffectiveDate(newValidityEnd);
            permit.setNote(String.format(
                "Renewed on %tF. Valid from %tF until %tF. %s", 
                now, newValidityStart, newValidityEnd, remarks != null ? remarks : ""
            ));
            
            if (!permitDAO.updatePermit(permit)) {
                throw new Exception("Failed to update permit after approval");
            }
        } else {
            // Deny renewal
            renewal.setStatus("denied");
            
            // Suspend the permit
            permit.setStatus("suspended");
            permit.setStatusEffectiveDate(now);
            permit.setNote(String.format(
                "Renewal denied. Failed inspection on %tF. %s", 
                now, remarks != null ? remarks : ""
            ));
            
            if (!permitDAO.updatePermit(permit)) {
                throw new Exception("Failed to update permit after denial");
            }
        }
        
        // Update renewal application status
        if (!renewalDAO.updateRenewalApplication(renewal)) {
            throw new Exception("Failed to update renewal application status");
        }
        
        return true;
    }
    
    /**
     * Calculates surcharge for late renewal based on permit expiry date.
     * Base surcharge: 25% of base fee if expired
     * Additional surcharge: 10% of base fee if more than 60 days late
     * 
     * @param permit the permit being renewed
     * @param type the permit type containing fee information
     * @return the calculated surcharge amount
     */
    public double calculateSurcharge(PermitModel permit, PermitTypeModel type) {
        try {
            // Use validity_end for expiry check
            Date expiry = permit.getValidityEnd();
            if (expiry == null) {
                return 0.0; // No expiry date means no surcharge
            }
            
            Date now = new Date();
            
            // If not expired yet, no surcharge
            if (!now.after(expiry)) {
                return 0.0;
            }
            
            // Calculate days late
            long millisLate = now.getTime() - expiry.getTime();
            long daysLate = millisLate / (1000 * 60 * 60 * 24);
            
            double baseFee = type.getFeeSchedule().getBaseFee();
            double surcharge = 0.0;
            
            // Base surcharge: 25% for any late renewal
            surcharge = baseFee * 0.25;
            
            // Additional surcharge: 10% if more than 60 days late
            if (daysLate > 60) {
                surcharge += baseFee * 0.10;
            }
            
            return surcharge;
        } catch (Exception e) {
            System.err.println("Error calculating surcharge: " + e.getMessage());
            return 0.0;
        }
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Retrieves all businesses from the database.
     * 
     * @return list of all businesses
     * @throws Exception if database query fails
     */
    public List<BusinessModel> getAllBusinesses() throws Exception {
        return businessDAO.getAllBusinesses();
    }
    
    /**
     * Retrieves all permits for a specific business.
     * 
     * @param businessId the business ID
     * @return list of permits for the business
     */
    public List<PermitModel> getPermitsByBusiness(int businessId) {
        return permitDAO.getPermitsByBusinessID(businessId);
    }
    
    /**
     * Retrieves all renewal applications for a specific business.
     * 
     * @param businessId the business ID
     * @return list of renewal applications for the business
     */
    public List<PermitRenewalApplicationModel> getRenewalsByBusiness(int businessId) {
        return renewalDAO.getRenewalsByBusinessID(businessId);
    }
    
    /**
     * Checks if an inspection has been scheduled for a renewal application.
     * 
     * @param renewalId the renewal application ID
     * @return true if inspection is scheduled
     */
    public boolean isInspectionScheduled(int renewalId) {
        try {
            InspectionModel inspection = inspectionDAO.getInspectionByRenewal(renewalId);
            return inspection != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Retrieves a specific permit type by ID.
     * 
     * @param permitTypeId the permit type ID
     * @return the permit type model
     */
    public PermitTypeModel getPermitTypeByID(int permitTypeId) {
        return typeDAO.getPermitTypeByID(permitTypeId);
    }
    
    /**
     * Retrieves a specific renewal application by ID.
     * 
     * @param renewalId the renewal application ID
     * @return the renewal application model
     */
    public PermitRenewalApplicationModel getRenewalByID(int renewalId) {
        return renewalDAO.getRenewalApplicationByID(renewalId);
    }
}