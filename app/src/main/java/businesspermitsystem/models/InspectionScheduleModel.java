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
}
