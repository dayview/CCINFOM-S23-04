package businesspermitsystem.views;

import businesspermitsystem.models.PermitModel;
import java.util.Date;
import java.util.List;

public class InitialPermitView {
    public void displayPermitList(List<PermitModel> permitModels) {
        System.out.println("Permits:");
        for (PermitModel permit : permitModels) {
            System.out.println(
                    "ID: " +  permit.getPermitID() +
                    ", Business ID: " + permit.getBusinessID() +
                    ", Permit Type ID: " + permit.getPermitTypeID() +
                    ", Status: " + permit.getStatus() +
                    ", Effective Date: " + permit.getStatusEffectiveDate()
            );
        }
    }

    public void displayPermitDetails(PermitModel permitModel) {
        System.out.println("Permit Details:");
        System.out.println("ID: " + permitModel.getPermitID());
        System.out.println("Business ID: " + permitModel.getBusinessID());
        System.out.println("Permit Type ID: " + permitModel.getPermitTypeID());
        System.out.println("Status: " + permitModel.getStatus());
        System.out.println("Status Effective Date: " + permitModel.getStatusEffectiveDate());
        System.out.println("Note: " + permitModel.getNote());
    }

    public PermitModel getInputForNewPermit() {
        // Placeholder returning dummy Permit at the moment
        return new PermitModel(0, 0, 0, "issued", (java.sql.Date) new Date(), "");
    }

    public PermitModel getInputForUpdatePermit(PermitModel permitModel) {
        // Placeholder returning unchanged permit at the moment
        return permitModel;
    }
}