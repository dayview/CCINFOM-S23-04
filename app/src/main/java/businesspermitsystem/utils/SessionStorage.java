package businesspermitsystem.utils;

import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.models.PermitApplicationModel;

/**
 * A temporary in-memory storage for selected objects used across
 * multi-step workflows in the Business Permit System.
 *
 * This utility class stores:
 * - The selected business
 * - The selected owner
 * - The selected permit type
 * - The selected permit application
 *
 * Stored data can be cleared using {@link #clear()}.
 */
public class SessionStorage {

    /** The currently selected business for the active workflow. */
    private static BusinessModel selectedBusiness;

    /** The currently selected owner associated with the business. */
    private static OwnerModel selectedOwner;

    /** The permit type selected during the permit application workflow. */
    private static PermitTypeModel selectedPermitType;

    /** The permit application selected or created in the workflow. */
    private static PermitApplicationModel selectedApplication;

    /**
     * Sets the selected business for the session.
     *
     * @param business the business selected by the user
     */
    public static void setSelectedBusiness(BusinessModel business) {
        selectedBusiness = business;
    }

    /**
     * Retrieves the business currently stored in the session.
     *
     * @return the selected business, or null if none stored
     */
    public static BusinessModel getSelectedBusiness() {
        return selectedBusiness;
    }

    /**
     * Stores the selected owner for the workflow session.
     *
     * @param owner the owner selected by the user
     */
    public static void setSelectedOwner(OwnerModel owner) {
        selectedOwner = owner;
    }

    /**
     * Retrieves the owner currently stored in the session.
     *
     * @return the selected owner, or null if none stored
     */
    public static OwnerModel getSelectedOwner() {
        return selectedOwner;
    }

    /**
     * Stores the selected permit type for the session.
     *
     * @param permitType the permit type chosen by the user
     */
    public static void setSelectedPermitType(PermitTypeModel permitType) {
        selectedPermitType = permitType;
    }

    /**
     * Retrieves the permit type stored in the current session.
     *
     * @return the selected permit type, or null if none stored
     */
    public static PermitTypeModel getSelectedPermitType() {
        return selectedPermitType;
    }

    /**
     * Stores the permit application associated with the current workflow.
     *
     * @param application the permit application object
     */
    public static void setSelectedApplication(PermitApplicationModel application) {
        selectedApplication = application;
    }

    /**
     * Retrieves the permit application currently stored in the session.
     *
     * @return the selected permit application, or null if none stored
     */
    public static PermitApplicationModel getSelectedApplication() {
        return selectedApplication;
    }

    /**
     * Clears all stored objects from the session.
     * This is used when completing or abandoning a workflow.
     */
    public static void clear() {
        selectedBusiness = null;
        selectedOwner = null;
        selectedPermitType = null;
        selectedApplication = null;
    }
}
