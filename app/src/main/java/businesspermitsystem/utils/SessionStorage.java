package businesspermitsystem.utils;

import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;

public class SessionStorage {

    private static BusinessModel selectedBusiness;
    private static OwnerModel selectedOwner;

    public static void setSelectedBusiness(BusinessModel business) {
        selectedBusiness = business;
    }

    public static BusinessModel getSelectedBusiness() {
        return selectedBusiness;
    }

    public static void setSelectedOwner(OwnerModel owner) {
        selectedOwner = owner;
    }

    public static OwnerModel getSelectedOwner() {
        return selectedOwner;
    }

    public static void clear() {
        selectedBusiness = null;
        selectedOwner = null;
    }
}
