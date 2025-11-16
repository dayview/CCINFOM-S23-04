package businesspermitsystem.views;

import java.util.ArrayList;
import businesspermitsystem.models.OwnerModel;

public class OwnerView {
    public void displayOwnerList(ArrayList<OwnerModel> owners) {
        System.out.println("Owners: ");
        for (OwnerModel owner : owners) {
            System.out.println(
                "ID: " + owner.getOwnerID() + 
                ", Last Name: " + owner.getLastName() + 
                ", First Name: " + owner.getFirstName() + 
                ", Middle Name: " + owner.getMiddleName() + 
                ", Contact Number: " + owner.getContactNo() +
                ", Email: " + owner.getEmail() +
                ", Government ID Type: " + owner.getGovID_type() + 
                ", Government ID Number: " + owner.getGovID_no() +
                ", TIN: " + owner.getTin() +
                ", Home Address: " + owner.getHomeAddress()
            );
        }
    }

    public void displayOwnerDetails(OwnerModel owner) {
        System.out.println("Owner Details:");
        System.out.println("ID: " + owner.getOwnerID());
        System.out.println("Last Name: " + owner.getLastName());
        System.out.println("First Name: " + owner.getFirstName());
        System.out.println("Middle Name: " + owner.getMiddleName());
        System.out.println("Contact Number: " + owner.getContactNo());
        System.out.println("Email: " + owner.getEmail());
        System.out.println("Government ID Type: " + owner.getGovID_type());
        System.out.println("Government ID Number: " + owner.getGovID_no());
        System.out.println("TIN: " + owner.getTin());
        System.out.println("Home Address: " + owner.getHomeAddress());
    }

    public OwnerModel getInputNewOwner(OwnerModel owner) {
        return new OwnerModel(0, "", "", "", "", "", "", "", "", "");
    }

    public OwnerModel getInputUpdateOwner(OwnerModel owner) {
        return owner;
    }
}
