package businesspermitsystem.controllers;

import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.views.PermitTypeView;
import java.util.List;

public class PermitTypeController {
    private PermitTypeDAO permitTypeDAO;
    private PermitTypeView permitTypeView;

    public PermitTypeController(PermitTypeDAO permitTypeDAO, PermitTypeView permitTypeView) {
        this.permitTypeDAO = permitTypeDAO;
        this.permitTypeView = permitTypeView;
    }

    public void listPermitTypes() {
        List<PermitTypeModel> permitTypeModels = permitTypeDAO.getAllPermitTypes();
        permitTypeView.displayPermitTypeList(permitTypeModels);
    }

    public void viewPermitType(int permitTypeID) {
        PermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            permitTypeView.displayPermitTypeDetails(permitTypeModel);
        } else {
            System.out.println("Permit Type not found.");
        }
    }

    public void createPermitType() {
        PermitTypeModel newPermitTypeModel = permitTypeView.getInputForNewPermitType();
        permitTypeDAO.addPermitType(newPermitTypeModel);
        System.out.println("Permit Type added successfully.");
    }

    public void updatePermitType(int permitTypeID) {
        PermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            PermitTypeModel updatedPermitTypeModel = permitTypeView.getInputForUpdatePermitType(permitTypeModel);
            permitTypeDAO.updatePermitType(updatedPermitTypeModel);
            System.out.println("Permit Type updated successfully.");
            } else {
                System.out.println("Permit Type not found.");
            }
        }

    public void deletePermitType(int permitTypeID) {
        boolean success = permitTypeDAO.deletePermitType(permitTypeID);
        if (success) {
            System.out.println("Permit Type deleted successfully.");
        } else {
            System.out.println("Permit Type not found or unable to delete.");
        }
    }
}