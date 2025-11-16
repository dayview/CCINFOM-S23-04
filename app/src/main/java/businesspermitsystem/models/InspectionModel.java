package businesspermitsystem.models;

import java.util.Date;

public class InspectionModel {
    private int inspectionID;
    private int renewalID;
    private int inspectorID;
    private Date inspectionDate;

    public InspectionModel(int inspectionID, int renewalID, int inspectorID, Date inspectionDate) {
        this.inspectionID = inspectionID;
        this.renewalID = renewalID;
        this.inspectorID = inspectorID;
        this.inspectionDate = inspectionDate;
    }

    public int getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(int inspectionID) {
        this.inspectionID = inspectionID;
    }

    public int getRenewalID() {
        return renewalID;
    }

    public void setRenewalID(int renewalID) {
        this.renewalID = renewalID;
    }

    public int getInspectorID() {
        return inspectorID;
    }

    public void setInspectornID(int inspectorID) {
        this.inspectorID = inspectorID;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }
}
