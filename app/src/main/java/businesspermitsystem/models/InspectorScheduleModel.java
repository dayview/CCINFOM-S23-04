package businesspermitsystem.models;

import java.time.LocalDate;

public class InspectorScheduleModel {

    private int scheduleId;
    private int inspectorId;
    private int businessId;
    private LocalDate inspectionDate;
    private String status;

    public InspectorScheduleModel() {}

    public InspectorScheduleModel(int scheduleId, int inspectorId, int businessId,
                                  LocalDate inspectionDate, String status) {
        this.scheduleId = scheduleId;
        this.inspectorId = inspectorId;
        this.businessId = businessId;
        this.inspectionDate = inspectionDate;
        this.status = status;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(int inspectorId) {
        this.inspectorId = inspectorId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
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
