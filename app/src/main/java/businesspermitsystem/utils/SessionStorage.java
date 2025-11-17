package businesspermitsystem.utils;

import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.models.PermitTypeModel;

public class SessionStorage {

    private static BusinessModel selectedBusiness;
    private static OwnerModel selectedOwner;
    private static PermitTypeModel selectedPermitType;

    // --- BUSINESS ---
    public static void setSelectedBusiness(BusinessModel business) {
        selectedBusiness = business;
    }

    public static BusinessModel getSelectedBusiness() {
        return selectedBusiness;
    }

    // --- OWNER ---
    public static void setSelectedOwner(OwnerModel owner) {
        selectedOwner = owner;
    }

    public static OwnerModel getSelectedOwner() {
        return selectedOwner;
    }

    // --- PERMIT TYPE ---
    public static void setSelectedPermitType(PermitTypeModel permitType) {
        selectedPermitType = permitType;
    }

    public static PermitTypeModel getSelectedPermitType() {
        return selectedPermitType;
    }

    // --- CLEAR SESSION ---
    public static void clear() {
        selectedBusiness = null;
        selectedOwner = null;
        selectedPermitType = null;
    }
}
