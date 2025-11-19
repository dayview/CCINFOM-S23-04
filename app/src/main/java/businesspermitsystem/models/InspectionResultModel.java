package businesspermitsystem.models;

/**
 * The {@code InspectionResultModel} stores the findings and official outcome
 * of a completed business inspection.
 */
public class InspectionResultModel {

    private int inspectionId;   
    private int scheduleId;     
    private String result;      
    private String remarks;   


    public InspectionResultModel() {}

    public InspectionResultModel(int inspectionId, int scheduleId, String result, String remarks) {
        this.inspectionId = inspectionId;
        this.scheduleId = scheduleId;
        this.result = result;
        this.remarks = remarks;
    }

    // --- Getters and Setters ---
    public int getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(int inspectionId) {
        this.inspectionId = inspectionId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
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