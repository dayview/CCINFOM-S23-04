package businesspermitsystem.views;

import java.util.List;
import businesspermitsystem.models.FeeScheduleModel;

public class FeeScheduleView {
    public void displayFeeScheduleList(List<FeeScheduleModel> feeScheduleModels) {
        System.out.println("Fee Schedules:");
        for (FeeScheduleModel fee : feeScheduleModels) {
            System.out.println(
                    "ID: " + fee.getID() +
                    ", Base Fee: " + fee.getBaseFee() +
                    ", Surcharge Rule: " + fee.getSurchargeRule() +
                    ", Validity (Months): " + fee.getValidityMonths()
            );
        }
    }

    public void displayFeeScheduleDetails(FeeScheduleModel feeScheduleModel) {
        System.out.println("Fee Schedule Details:");
        System.out.println("ID: " + feeScheduleModel.getID());
        System.out.println("Base Fee: " + feeScheduleModel.getBaseFee());
        System.out.println("Surcharge Rule: " + feeScheduleModel.getSurchargeRule());
        System.out.println("Validity (Months): " + feeScheduleModel.getValidityMonths());
        System.out.println("Document Requirements: " + feeScheduleModel.getDocumentRequirements());
    }

    public FeeScheduleModel getInputForNewFeeSchedule() {
        // Implementation to capture user input to create a new FeeSchedule
        // For demonstration, return as a test dummy FeeSchedule object
        return new FeeScheduleModel(0, 0.0, "", 0, "");
    }

    public FeeScheduleModel getInputForUpdateFeeSchedule(FeeScheduleModel feeScheduleModel) {
        // Implementation to capture user input to update the given FeeSchedule
        // For demonstration, simply returning the passed object
        return feeScheduleModel;
    }
}
