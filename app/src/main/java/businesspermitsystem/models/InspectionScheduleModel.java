package businesspermitsystem.models;

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
    private int inspection_date;
    /**
     * Status of the given inspection (e.g. Scheduled, In Progress, Complete, Failed)
     */
    private int status;
    public InspectionScheduleModel(int scheduleID, int businessID, int inspectorID, int inspection_date, int status) {
        this.scheduleID = scheduleID;
        this.businessID = businessID;
        this.inspectorID = inspectorID;
        this.inspection_date = inspection_date;
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
    public int getInspection_date() {
        return inspection_date;
    }
    public void setInspection_date(int inspection_date) {
        this.inspection_date = inspection_date;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
