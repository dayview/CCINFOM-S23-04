package businesspermitsystem.controllers;

import businesspermitsystem.models.FeeScheduleModel;
import businesspermitsystem.views.FeeScheduleView;
import businesspermitsystem.db.FeeScheduleDAO;
import java.util.List;

public class FeeScheduleController {
    private FeeScheduleDAO feeScheduleDAO;
    private FeeScheduleView feeScheduleView;

    public FeeScheduleController(FeeScheduleDAO service, FeeScheduleView view) {
        this.feeScheduleDAO = service;
        this.feeScheduleView = view;
    }

    public void listFeeSchedules() {
        List<FeeScheduleModel> feeScheduleModels = feeScheduleDAO.getAllFeeSchedules();
        feeScheduleView.displayFeeScheduleList(feeScheduleModels);
    }

    public void viewFeeSchedule(int feeScheduleID) {
        FeeScheduleModel feeScheduleModel = feeScheduleDAO.getFeeScheduleByID(feeScheduleID);
        if (feeScheduleModel != null) {
            feeScheduleView.displayFeeScheduleDetails(feeScheduleModel);
        } else {
            System.out.println("Fee Schedule not found.");
        }
    }

    public void createFeeSchedule() {
        FeeScheduleModel newFeeScheduleModel = feeScheduleView.getInputForNewFeeSchedule();
        feeScheduleDAO.addFeeSchedule(newFeeScheduleModel);
        System.out.println("Fee Schedule added successfully.");
    }

    public void updateFeeSchedule(int feeScheduleID) {
        FeeScheduleModel feeScheduleModel = feeScheduleDAO.getFeeScheduleByID(feeScheduleID);
        if (feeScheduleModel != null) {
            FeeScheduleModel updatedFeeScheduleModel = feeScheduleView.getInputForUpdateFeeSchedule(feeScheduleModel);
            feeScheduleDAO.updateFeeSchedule(updatedFeeScheduleModel);
            System.out.println("Fee Schedule updated successfully.");
        } else {
            System.out.println("Fee Schedule not found.");
        }
    }

    public void deleteFeeSchedule(int feeScheduleID) {
        boolean success = feeScheduleDAO.deleteFeeSchedule(feeScheduleID);
        if (success) {
            System.out.println("Fee Schedule deleted successfully.");
        } else {
            System.out.println("Fee Schedule not found or unable to delete.");
        }
    }
}