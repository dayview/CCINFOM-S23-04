package businesspermitsystem.models;

public class InspectorResultModel {

    private int inspectionResultId;
    private int scheduleId;
    private String result;    // "Pass" or "Fail"
    private String remarks;

    public InspectorResultModel() {}

    public InspectorResultModel(int inspectionResultId, int scheduleId, String result, String remarks) {
        this.inspectionResultId = inspectionResultId;
        this.scheduleId = scheduleId;
        this.result = result;
        this.remarks = remarks;
    }

    public int getInspectionResultId() {
        return inspectionResultId;
    }

    public void setInspectionResultId(int inspectionResultId) {
        this.inspectionResultId = inspectionResultId;
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
