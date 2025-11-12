package businesspermitsystem.controllers;

import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.services.PermitTypeService;
import businesspermitsystem.views.PermitTypeView;
import java.util.List;

public class PermitTypeController {
    private PermitTypeService permitTypeService;
    private PermitTypeView permitTypeView;

    public PermitTypeController(PermitTypeService permitTypeService, PermitTypeView permitTypeView) {
        this.permitTypeService = permitTypeService;
        this.permitTypeView = permitTypeView;
    }

    public void listPermitTypes() {
        List<PermitTypeModel> permitTypeModels = permitTypeService.getAllPermitTypes();
        permitTypeView.displayPermitTypeList(permitTypeModels);
    }

    public void viewPermitType(int permitTypeID) {
        PermitTypeModel permitTypeModel = permitTypeService.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            permitTypeView.displayPermitTypeDetails(permitTypeModel);
        } else {
            System.out.println("Permit Type not found.");
        }
    }

    public void createPermitType() {
        PermitTypeModel newPermitTypeModel = permitTypeView.getInputForNewPermitType();
        permitTypeService.addPermitType(newPermitTypeModel);
        System.out.println("Permit Type added successfully.");
    }

    public void updatePermitType(int permitTypeID) {
        PermitTypeModel permitTypeModel = permitTypeService.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            PermitTypeModel updatedPermitTypeModel = permitTypeView.getInputForUpdatePermitType(permitTypeModel);
            permitTypeService.updatePermitType(updatedPermitTypeModel);
            System.out.println("Permit Type updated successfully.");
            } else {
                System.out.println("Permit Type not found.");
            }
        }

    public void deletePermitType(int permitTypeID) {
        boolean success = permitTypeService.deletePermitType(permitTypeID);
        if (success) {
            System.out.println("Permit Type deleted successfully.");
        } else {
            System.out.println("Permit Type not found or unable to delete.");
        }
    }
}