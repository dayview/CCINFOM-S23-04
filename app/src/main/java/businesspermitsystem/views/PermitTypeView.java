package businesspermitsystem.views;

import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.models.FeeScheduleModel;
import java.util.List;

public class PermitTypeView {
    public void displayPermitTypeList(List<PermitTypeModel> permitTypeModels) {
        System.out.println("Permit Types:");
        for (PermitTypeModel permitType : permitTypeModels) {
            FeeScheduleModel fee = permitType.getFeeSchedule();
            System.out.println(
                    "ID: " + permitType.getPermitTypeID() +
                    ", Name: " + permitType.getPermitName() +
                    ", Base Fee: " + (fee != null ? fee.getBaseFee() : "N/A") +
                    ", Validity (Months): " + permitType.getValidityMonths()
            );
        }
    }

    public void displayPermitTypeDetails(PermitTypeModel permitTypeModel) {
        System.out.println("Permit Type Details:");
        System.out.println("ID: " + permitTypeModel.getID());
        System.out.println("Name: " + permitTypeModel.getName());

        FeeScheduleModel fee = permitTypeModel.getFeeSchedule();
        if (fee != null) {
            System.out.println("Base Fee: " + fee.getBaseFee());
            System.out.println("Surcharge Rule: " + fee.getSurchargeRule());
        }
        System.out.println("Validity (Months): " + permitTypeModel.getValidityMonths());
        System.out.println("Document Requirements: " + permitTypeModel.getDocumentRequirements());
    }

    public PermitTypeModel getInputForNewPermitType() {
        // Implementation to get input for new PermitType, returning dummy at the moment
        return new PermitTypeModel(0, "", 0.0, "", 0, "", null);
    }

    public PermitTypeModel getInputForUpdatePermitType(PermitTypeModel permitTypeModel) {
        // Implementation to update PermitType data for user input, returning unchanged at the moment
        return permitTypeModel;
    }
}
