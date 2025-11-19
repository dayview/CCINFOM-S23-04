package businesspermitsystem.services;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    
    // STEP 1: Apply
    public int applyForRenewal(int businessId, int permitId) throws Exception {
        BusinessModel business = businessDAO.getBusinessByID(businessId);
        if (business == null || !"Active".equals(business.getStatus())) {
            throw new Exception("Business not active");
        }
        
        PermitModel permit = permitDAO.getPermitByID(permitId);
        if (permit == null) {
            throw new Exception("Permit not found");
        }
        
        PermitTypeModel type = typeDAO.getPermitTypeByID(permit.getPermitTypeID());
        if (type == null) {
            throw new Exception("Permit type not found");
        }
        
        double fee = type.getFeeSchedule().getBaseFee();
        double surcharge = calculateSurcharge(permit, type);
        double total = fee + surcharge;
        
        PermitRenewalApplicationModel renewal = new PermitRenewalApplicationModel(
            0, businessId, permitId, new Date(), fee, surcharge, total, "pending"
        );
        
        return renewalDAO.addRenewalGetID(renewal);
    }
    
    // STEP 2: Payment
    public boolean recordPayment(int renewalId, double amount, String method) throws Exception {
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            throw new Exception("Renewal not found");
        }
        
        if (amount < renewal.getTotalAmount()) {
            throw new Exception("Amount less than total due");
        }
        
        PaymentModel payment = new PaymentModel(0, renewalId, amount, method, new Date());
        int paymentId = paymentDAO.addPaymentGetID(payment);
        
        if (paymentId > 0) {
            renewal.setStatus("paid");
            renewalDAO.updateRenewalApplication(renewal);
            return true;
        }
        
        return false;
    }
    
    // STEP 3: Schedule
    public boolean scheduleInspection(int renewalId, int inspectorId, Date date) throws Exception {
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            throw new Exception("Renewal not found");
        }
        
        if (!"paid".equals(renewal.getStatus())) {
            throw new Exception("Payment not completed");
        }
        
        InspectionModel inspection = new InspectionModel(0, renewalId, inspectorId, date);
        int inspectionId = inspectionDAO.addInspectionGetID(inspection);
        
        if (inspectionId <= 0) {
            return false;
        }
        
        InspectionScheduleModel schedule = new InspectionScheduleModel();
        schedule.setBusinessID(renewal.getBusinessID());
        schedule.setInspectorID(inspectorId);
        schedule.setInspectionDate(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        schedule.setStatus("Scheduled");
        
        return scheduleDAO.addSchedule(schedule);
    }
    
    // STEP 4: Finalize
    public boolean finalizeRenewal(int renewalId, int scheduleId, String result, String remarks) throws Exception {
        InspectionResultModel inspectionResult = new InspectionResultModel();
        inspectionResult.setScheduleId(scheduleId);
        inspectionResult.setResult(result);
        inspectionResult.setRemarks(remarks);
        
        if (!resultDAO.addResult(inspectionResult)) {
            return false;
        }
        
        InspectionScheduleModel schedule = scheduleDAO.getScheduleByID(scheduleId);
        if (schedule != null) {
            schedule.setStatus("Completed");
            scheduleDAO.updateSchedule(schedule);
        }
        
        PermitRenewalApplicationModel renewal = renewalDAO.getRenewalApplicationByID(renewalId);
        if (renewal == null) {
            return false;
        }
        
        PermitModel permit = permitDAO.getPermitByID(renewal.getPreviousPermitID());
        if (permit == null) {
            return false;
        }
        
        if ("PASS".equalsIgnoreCase(result)) {
            renewal.setStatus("approved");
            renewalDAO.updateRenewalApplication(renewal);
            
            PermitTypeModel type = typeDAO.getPermitTypeByID(permit.getPermitTypeID());
            Date start = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            cal.add(Calendar.MONTH, type.getValidityMonths());
            Date expiry = cal.getTime();
            
            permit.setStatus("renewed");
            permit.setStatusEffectiveDate(expiry);
            permit.setNote("Renewed on " + start + ". Valid until " + expiry + ". " + remarks);
            permitDAO.updatePermit(permit);
        } else {
            renewal.setStatus("denied");
            renewalDAO.updateRenewalApplication(renewal);
            
            permit.setStatus("suspended");
            permit.setNote("Renewal denied. Failed inspection on " + new Date() + ". " + remarks);
            permitDAO.updatePermit(permit);
        }
        
        return true;
    }
    
    // Calculate surcharge
    public double calculateSurcharge(PermitModel permit, PermitTypeModel type) {
        try {
            Date expiry = permit.getStatusEffectiveDate();
            Date now = new Date();
            
            if (!now.after(expiry)) {
                return 0.0;
            }
            
            long daysLate = (now.getTime() - expiry.getTime()) / (1000 * 60 * 60 * 24);
            double baseFee = type.getFeeSchedule().getBaseFee();
            double surcharge = baseFee * 0.25;
            
            if (daysLate > 60) {
                surcharge += baseFee * 0.10;
            }
            
            return surcharge;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    // Helper methods
    public List<BusinessModel> getAllBusinesses() throws Exception {
        return businessDAO.getAllBusinesses();
    }
    
    public List<PermitModel> getPermitsByBusiness(int businessId) {
        return permitDAO.getPermitsByBusinessID(businessId);
    }
    
    public List<PermitRenewalApplicationModel> getRenewalsByBusiness(int businessId) {
        return renewalDAO.getRenewalsByBusinessID(businessId);
    }
    
    public boolean isInspectionScheduled(int renewalId) {
        try {
            InspectionModel inspection = inspectionDAO.getInspectionByRenewal(renewalId);
            return inspection != null;
        } catch (Exception e) {
            return false;
        }
    }
}