package businesspermitsystem.models;
import java.util.Scanner;

/* This class is exclusively handling the permit data (types & data) of a business */

/* Load PermitType (master record), pick the active FeeSchedule for that type and date
   Pick the active FeeSchedule for that type and date
   subtotal = feeSchedule.computeSubtotal(permitType, feeContext)
   taxes = taxCalculator.computeTaxes(subtotal, taxContent)
   total = subtotal + taxes.totalTax
 */

/* Edge cases to handle
 * Late renewal penalties (surcharge vs separate fee line)
 * Tax-exempt applicants
 * Tax-inclusive jurisdictions (prices include VAT)
 * Caps/maximum surcharge
 * Mid-year rule changes (effective dates)
 * Backdated filings (use filing/approval date consistently)
 */

public class PermitType {
    public enum permitTypeID {
        SOLE_PROPRIETORSHIP, PARTNERSHIP, CORPORATION, BRANCH
    }

    private String permitID;
    private String permitName;
    private String validityMonths;
    private String documentRequirements;

    /* logic for sole proprietorship
    * 1. fill up BIR Form No. 1901 (use exclusive inputs)
    * 2. any government-issued ID (PhilID/ePhilID, Passport, Driver's License/eDriver's License)
    *    that shows the name, address, and birthdate of the applicant, in case the ID has no
    *    address, any proof of residence or business address -- 1 photocopy
    *    + valid PRC ID and government ID showing address or proof of residence or business address
    *    (in case of the practice of profession regulated by PRC) -- 1 photocopy
    * 3. buy BIR printed invoice (available for sale at the new business registrant counter) or
    *    final clear sample of OWN invoices (1 original) -- in case taxpayer-applicant will opt to print
    *    its own invoices, taxpayer-applicant should choose an Accredited Printer who will print the
    *    invoices
    *
    * fees to be paid: payment of 30 (loose stamp) (DST) to be affixed on the certificate of registration
    *                  procured printing cost of BPI, if opted to use
    *
    *
    * */

    /* logic for corporations, partnerships */

    /* logic for branch */

}
