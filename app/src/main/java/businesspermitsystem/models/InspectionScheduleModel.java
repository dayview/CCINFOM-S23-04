package businesspermitsystem.models;

import java.time.LocalDate;

/**
 * The model for the inspection schedule from the data base
 */
public class InspectionScheduleModel {
    /**
     * The primary key and unique identifier of each schedule
     */
    private int scheduleID;
    /**
     * The foreign key that identifies the ID of the business being inspected
     */
    private int businessID;
    /**
     * The foreign key that identifies the ID of the inspector assigned
     */
    private int inspectorID;
    /**
     * The date of the inspection
     */
    private LocalDate inspectionDate;
    /**
     * Status of the given inspection (e.g. Scheduled, In Progress, Complete, Failed)
     */
    private String status; 

    /**
     * Default constructor for use by DAOs when loading data.
     */
    public InspectionScheduleModel() {
    }


    public InspectionScheduleModel(int scheduleID, int businessID, int inspectorID, LocalDate inspectionDate, String status) {
        this.scheduleID = scheduleID;
        this.businessID = businessID;
        this.inspectorID = inspectorID;
        this.inspectionDate = inspectionDate;
        this.status = status;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getBusinessID() {
        return businessID;
    }

    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    public int getInspectorID() {
        return inspectorID;
    }

    public void setInspectorID(int inspectorID) {
        this.inspectorID = inspectorID;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}