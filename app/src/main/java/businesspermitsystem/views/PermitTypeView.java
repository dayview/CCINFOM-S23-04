package businesspermitsystem.views;

import businesspermitsystem.models.PermitTypeModel;

import java.util.List;

public class PermitTypeView {

    // Display list of permit types
    public void displayPermitTypeList(List<PermitTypeModel> permitTypeModels) {
        System.out.println("Permit Types:");

        for (PermitTypeModel permitType : permitTypeModels) {
            System.out.println(
                    "ID: " + permitType.getPermitTypeId() +
                            ", Name: " + permitType.getPermitName() +
                            ", Base Fee: " + permitType.getBaseFee() +
                            ", Surcharge: " + permitType.getSurchargeRule() +
                            ", Validity (Months): " + permitType.getValidityMonths()
            );
        }
    }

    // Display detailed info for a single permit type
    public void displayPermitTypeDetails(PermitTypeModel permitTypeModel) {
        System.out.println("Permit Type Details:");
        System.out.println("ID: " + permitTypeModel.getPermitTypeId());
        System.out.println("Name: " + permitTypeModel.getPermitName());
        System.out.println("Base Fee: " + permitTypeModel.getBaseFee());
        System.out.println("Surcharge Rule: " + permitTypeModel.getSurchargeRule());
        System.out.println("Validity (Months): " + permitTypeModel.getValidityMonths());
        System.out.println("Document Requirements: " + permitTypeModel.getDocumentRequirements());
    }

    // Dummy input for creating a new permit type
    public PermitTypeModel getInputForNewPermitType() {
        // Return a dummy object for now (not used in JavaFX version)
        return new PermitTypeModel(0, "", null, "", 0, "");
    }

    // Dummy input for updating a permit type
    public PermitTypeModel getInputForUpdatePermitType(PermitTypeModel permitTypeModel) {
        return permitTypeModel; // Placeholder
    }
}
