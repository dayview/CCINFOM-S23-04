package businesspermitsystem.utils;

import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.models.PermitTypeModel;

public class SessionStorage {

    private static BusinessModel selectedBusiness;
    private static OwnerModel selectedOwner;
    private static PermitTypeModel selectedPermitType;

    //for the businesses
    public static void setSelectedBusiness(BusinessModel business) {
        selectedBusiness = business;
    }

    public static BusinessModel getSelectedBusiness() {
        return selectedBusiness;
    }

    //for the owners
    public static void setSelectedOwner(OwnerModel owner) {
        selectedOwner = owner;
    }

    public static OwnerModel getSelectedOwner() {
        return selectedOwner;
    }

    //for the permit type
    public static void setSelectedPermitType(PermitTypeModel permitType) {
        selectedPermitType = permitType;
    }

    public static PermitTypeModel getSelectedPermitType() {
        return selectedPermitType;
    }

    //clear the session
    public static void clear() {
        selectedBusiness = null;
        selectedOwner = null;
        selectedPermitType = null;
    }
}
