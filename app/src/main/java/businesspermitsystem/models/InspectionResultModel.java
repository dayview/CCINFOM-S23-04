package businesspermitsystem.models;

/**
 * 
 */
public class InspectionResultModel {
    /**
     * The main primary key and unique identifier of each inspection ID
     */
    private int inspectionID;
    /**
     * The foreign key that identifies the Inspection Schedule of this result
     */
    private int scheduleID;
    /**
     * The foreign key that identifies the Inspector that created this result
     */
    private int inspectorID;
    /**
     * The result of the inspection(e.g pass, fail)
     */
    private String result;
    /**
     * Comments of the inspector about the inspection
     */
    private String remarks;
}   
