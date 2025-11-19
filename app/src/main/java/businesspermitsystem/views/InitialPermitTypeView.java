package businesspermitsystem.views;

import businesspermitsystem.models.InitialPermitTypeModel;
import java.util.List;

public class InitialPermitTypeView {

    public void displayPermitTypeList(List<InitialPermitTypeModel> permitTypeModels) {
        System.out.println("Permit Types:");
        for (InitialPermitTypeModel permitType : permitTypeModels) {
            System.out.println(
                    "ID: " + permitType.getPermitTypeId() +
                            ", Name: " + permitType.getPermitName() +
                            ", Base Fee: " + permitType.getBaseFee() +
                            ", Surcharge: " + permitType.getSurchargeRule() +
                            ", Validity (Months): " + permitType.getValidityMonths()
            );
        }
    }

    public void displayPermitTypeDetails(InitialPermitTypeModel permitTypeModel) {
        System.out.println("Permit Type Details:");
        System.out.println("ID: " + permitTypeModel.getPermitTypeId());
        System.out.println("Name: " + permitTypeModel.getPermitName());
        System.out.println("Base Fee: " + permitTypeModel.getBaseFee());
        System.out.println("Surcharge Rule: " + permitTypeModel.getSurchargeRule());
        System.out.println("Validity (Months): " + permitTypeModel.getValidityMonths());
        System.out.println("Document Requirements: " + permitTypeModel.getDocumentRequirements());
        System.out.println("Fee Schedule ID: " + permitTypeModel.getFeeScheduleId());
    }

    public InitialPermitTypeModel getInputForNewPermitType() {
        return new InitialPermitTypeModel(0, "", null, "", 0, "", 0);
    }

    public InitialPermitTypeModel getInputForUpdatePermitType(InitialPermitTypeModel permitTypeModel) {
        return permitTypeModel;
    }
}
