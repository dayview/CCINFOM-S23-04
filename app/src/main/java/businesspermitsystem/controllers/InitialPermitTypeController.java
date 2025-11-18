package businesspermitsystem.controllers;

import businesspermitsystem.models.InitialPermitTypeModel;
import businesspermitsystem.db.InitialPermitTypeDAO;
import businesspermitsystem.views.InitialPermitTypeView;
import java.util.List;

/**
 * Controller class responsible for managing permit type operations such as
 * listing, viewing, creating, updating, and deleting permit types.
 *
 * Works together with InitialPermitTypeDAO (data access) and
 * InitialPermitTypeView (console-based output/input).
 */
public class InitialPermitTypeController {

    private InitialPermitTypeDAO permitTypeDAO;
    private InitialPermitTypeView permitTypeView;

    /**
     * Constructs the controller with the required DAO and View.
     *
     * @param permitTypeDAO  data access object for permit types
     * @param permitTypeView console view for displaying permit type data
     */
    public InitialPermitTypeController(InitialPermitTypeDAO permitTypeDAO, InitialPermitTypeView permitTypeView) {
        this.permitTypeDAO = permitTypeDAO;
        this.permitTypeView = permitTypeView;
    }

    /**
     * Retrieves and displays all permit types.
     */
    public void listPermitTypes() {
        List<InitialPermitTypeModel> permitTypeModels = permitTypeDAO.getAllPermitTypes();
        permitTypeView.displayPermitTypeList(permitTypeModels);
    }

    /**
     * Displays the details of a specific permit type.
     *
     * @param permitTypeID ID of the permit type to view
     */
    public void viewPermitType(int permitTypeID) {
        InitialPermitTypeModel permitTypeModel = permitTypeDAO.getPermitTypeByID(permitTypeID);
        if (permitTypeModel != null) {
            permitTypeView.displayPermitTypeDetails(permitTypeModel);
        } else {
            System.out.println("Permit Type not found.");
        }
    }

    /**
     * Creates a new permit type using user-provided input.
     */
    public void createPermitType() {
        InitialPermitTypeModel newPermitTypeModel = permitTypeView.getInputForNewPermitType();
        permitTypeDAO.addPermitType(newPermitTypeModel);
        System.out.println("Permit Type added successfully.");
    }

    /**
     * Updates an existing permit type.
     *
     * @param permitTypeID ID of the permit type to update
     */
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

    /**
     * Deletes a permit type by ID.
     *
     * @param permitTypeID ID of the permit type to delete
     */
    public void deletePermitType(int permitTypeID) {
        boolean success = permitTypeDAO.deletePermitType(permitTypeID);
        if (success) {
            System.out.println("Permit Type deleted successfully.");
        } else {
            System.out.println("Permit Type not found or unable to delete.");
        }
    }
}
