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

    public InspectionResultModel(int inspectionID, int scheduleID, int inspectorID, String result, String remarks) {
        this.inspectionID = inspectionID;
        this.scheduleID = scheduleID;
        this.inspectorID = inspectorID;
        this.result = result;
        this.remarks = remarks;
    }

    public int getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(int inspectionID) {
        this.inspectionID = inspectionID;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getInspectorID() {
        return inspectorID;
    }

    public void setInspectorID(int inspectorID) {
        this.inspectorID = inspectorID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}   
