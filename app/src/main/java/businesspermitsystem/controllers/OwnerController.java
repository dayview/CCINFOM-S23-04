package businesspermitsystem.controllers;

import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.views.OwnerView;
import java.util.ArrayList;

public class OwnerController {
    private OwnerDAO ownerDAO;
    private OwnerView ownerView;

    public OwnerController(OwnerDAO service, OwnerView view) {
        this.ownerDAO = service;
        this.ownerView = view;
    }

    public void listOwners() {
        ArrayList<OwnerModel> owners = ownerDAO.getAllOwners();
        ownerView.displayOwnerList(owners);
    }

    public void viewOwner(int ownerID) {
        OwnerModel viewOwner = ownerDAO.getOwnerByID(ownerID);

        if (viewOwner != null) {
            ownerView.displayOwnerDetails(viewOwner);
        } else {
            System.out.println("Owner not found.");
        }
    }

    public void createOwner() {
        OwnerModel newOwner = ownerView.getInputNewOwner(null);
        ownerDAO.addOwner(newOwner);
        System.out.println("New owner added Successfully.");
    }

    public void updateOwner(int ownerID) {
        OwnerModel updateOwner = ownerDAO.getOwnerByID(ownerID);
        
        if (updateOwner != null) {
            updateOwner = ownerView.getInputUpdateOwner(updateOwner);
            ownerDAO.updateOwner(updateOwner);
            System.out.println("Owner details updated successfully.");
        } else {
            System.out.println("Owner not found.");
        }
    }

    public void deleteOwner(int ownerID) {
        boolean deleted = ownerDAO.deleteOwner(ownerID);

        if (deleted) {
            System.out.println("Owner deleted successfully.");
        } else {
            System.out.println("Unable to delete owner.");
        }
    }
}