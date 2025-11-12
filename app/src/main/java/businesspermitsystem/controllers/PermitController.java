package businesspermitsystem.controllers;

import businesspermitsystem.models.PermitModel;
import businesspermitsystem.services.PermitService;
import businesspermitsystem.views.PermitView;
import java.util.List;

public class PermitController {
    private PermitService permitService;
    private PermitView permitView;

    public PermitController(PermitService service, PermitView view) {
        this.permitService = service;
        this.permitView = view;
    }

    public void listPermits() {
        List<PermitModel> permitModels = permitService.getAllPermits();
        permitView.displayPermitList(permitModels);
    }

    public void viewPermit(int permitID) {
        PermitModel permitModel = permitService.getPermitByID(permitID);
        if (permitModel != null) {
            permitView.displayPermitDetails(permitModel);
        } else {
            System.out.println("Permit not found.");
        }
    }

    public void createPermit() {
        PermitModel newPermitModel = permitView.getInputForNewPermit();
        permitService.addPermit(newPermitModel);
        System.out.println("Permit added successfully.");
    }

    public void updatePermit(int permitID) {
        PermitModel permitModel = permitService.getPermitByID(permitID);
        if (permitModel != null) {
            PermitModel updatedPermitModel = permitView.getInputForUpdatePermit(permitModel);
            permitService.updatePermit(updatedPermitModel);
            System.out.println("Permit updated successfully.");
        } else {
            System.out.println("Permit not found.");
        }
    }

    public void deletePermit(int permitID) {
        boolean success = permitService.deletePermit(permitID);
        if (success) {
            System.out.println("Permit deleted successfully.");
        } else {
            System.out.println("Permit not found or unable to delete.");
        }
    }
}
