package businesspermitsystem.controllers;

import businesspermitsystem.models.PermitModel;
import businesspermitsystem.db.PermitDAO;
import businesspermitsystem.views.PermitView;
import java.util.List;

public class PermitController {
    private PermitDAO permitDAO;
    private PermitView permitView;

    public PermitController(PermitDAO service, PermitView view) {
        this.permitDAO = service;
        this.permitView = view;
    }

    public void listPermits() {
        List<PermitModel> permitModels = permitDAO.getAllPermits();
        permitView.displayPermitList(permitModels);
    }

    public void viewPermit(int permitID) {
        PermitModel permitModel = permitDAO.getPermitByID(permitID);
        if (permitModel != null) {
            permitView.displayPermitDetails(permitModel);
        } else {
            System.out.println("Permit not found.");
        }
    }

    public void createPermit() {
        PermitModel newPermitModel = permitView.getInputForNewPermit();
        permitDAO.addPermit(newPermitModel);
        System.out.println("Permit added successfully.");
    }

    public void updatePermit(int permitID) {
        PermitModel permitModel = permitDAO.getPermitByID(permitID);
        if (permitModel != null) {
            PermitModel updatedPermitModel = permitView.getInputForUpdatePermit(permitModel);
            permitDAO.updatePermit(updatedPermitModel);
            System.out.println("Permit updated successfully.");
        } else {
            System.out.println("Permit not found.");
        }
    }

    public void deletePermit(int permitID) {
        boolean success = permitDAO.deletePermit(permitID);
        if (success) {
            System.out.println("Permit deleted successfully.");
        } else {
            System.out.println("Permit not found or unable to delete.");
        }
    }
}