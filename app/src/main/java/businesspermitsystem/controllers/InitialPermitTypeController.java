package businesspermitsystem.controllers;

import businesspermitsystem.models.InitialPermitTypeModel;
import businesspermitsystem.db.InitialPermitTypeDAO;
import businesspermitsystem.views.InitialPermitTypeView;

import java.util.List;

public class InitialPermitTypeController {

    private InitialPermitTypeDAO permitTypeDAO;
    private InitialPermitTypeView permitTypeView;

    public InitialPermitTypeController(InitialPermitTypeDAO permitTypeDAO, InitialPermitTypeView permitTypeView) {
        this.permitTypeDAO = permitTypeDAO;
        this.permitTypeView = permitTypeView;
    }

    public void listPermitTypes() {
        List<InitialPermitTypeModel> permitTypeModels = permitTypeDAO.getAllPermitTypes();
        permitTypeView.displayPermitTypeList(permitTypeModels);
    }

    public void viewPermitType(int permitTypeID) {
        InitialPermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            permitTypeView.displayPermitTypeDetails(permitTypeModel);
        } else {
            System.out.println("Permit Type not found.");
        }
    }

    public void createPermitType() {
        InitialPermitTypeModel newPermitTypeModel = permitTypeView.getInputForNewPermitType();
        permitTypeDAO.addPermitType(newPermitTypeModel);
        System.out.println("Permit Type added successfully.");
    }

    public void updatePermitType(int permitTypeID) {
        InitialPermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            InitialPermitTypeModel updatedPermitTypeModel =
                    permitTypeView.getInputForUpdatePermitType(permitTypeModel);
            permitTypeDAO.updatePermitType(updatedPermitTypeModel);
            System.out.println("Permit Type updated successfully.");
        } else {
            System.out.println("Permit Type not found.");
        }
    }

    public void deletePermitType(int permitTypeID) {
        InitialPermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            boolean success = permitTypeDAO.deletePermitType(permitTypeID, permitTypeModel.getFeeScheduleId());
            if (success) {
                System.out.println("Permit Type deleted successfully.");
            } else {
                System.out.println("Unable to delete Permit Type.");
            }
        } else {
            System.out.println("Permit Type not found.");
        }
    }
}
