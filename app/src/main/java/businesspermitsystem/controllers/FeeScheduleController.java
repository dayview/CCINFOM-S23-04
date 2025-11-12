package businesspermitsystem.controllers;

import businesspermitsystem.models.FeeScheduleModel;
import businesspermitsystem.views.FeeScheduleView;
import businesspermitsystem.services.FeeScheduleService;
import java.util.List;

public class FeeScheduleController {
    private FeeScheduleService feeScheduleService;
    private FeeScheduleView feeScheduleView;

    public FeeScheduleController(FeeScheduleService service, FeeScheduleView view) {
        this.feeScheduleService = service;
        this.feeScheduleView = view;
    }

    public void listFeeSchedules() {
        List<FeeScheduleModel> feeScheduleModels = feeScheduleService.getAllFeeSchedules();
        feeScheduleView.displayFeeScheduleList(feeScheduleModels);
    }

    public void viewFeeSchedule(int feeScheduleID) {
        FeeScheduleModel feeScheduleModel = feeScheduleService.getFeeScheduleByID(feeScheduleID);
        if (feeScheduleModel != null) {
            feeScheduleView.displayFeeScheduleDetails(feeScheduleModel);
        } else {
            System.out.println("Fee Schedule not found.");
        }
    }

    public void createFeeSchedule() {
        FeeScheduleModel newFeeScheduleModel = feeScheduleView.getInputForNewFeeSchedule();
        feeScheduleService.addFeeSchedule(newFeeScheduleModel);
        System.out.println("Fee Schedule added successfully.");
    }

    public void updateFeeSchedule(int feeScheduleID) {
        FeeScheduleModel feeScheduleModel = feeScheduleService.getFeeScheduleByID(feeScheduleID);
        if (feeScheduleModel != null) {
            FeeScheduleModel updatedFeeScheduleModel = feeScheduleView.getInputForUpdateFeeSchedule(feeScheduleModel);
            feeScheduleService.updateFeeSchedule(updatedFeeScheduleModel);
            System.out.println("Fee Schedule updated successfully.");
        } else {
            System.out.println("Fee Schedule not found.");
        }
    }

    public void deleteFeeSchedule(int feeScheduleID) {
        boolean success = feeScheduleService.deleteFeeSchedule(feeScheduleID);
        if (success) {
            System.out.println("Fee Schedule deleted successfully.");
        } else {
            System.out.println("Fee Schedule not found or unable to delete.");
        }
    }
}