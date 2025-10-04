package businesspermitsystem.models;

enum structureType {
    SOLE_PROPRIETORSHIP,
    PARTNERSHIP,
    CORPORATION,
    ONE_PERSON_CORPORATION;
}

/* DTI Business Name Registration - Step-by-Step w/ Test Cases
    1. Name Search @ MySQL Database
        1a. At least two alternative names
        1b. Will not work if trade name has "Corporation", "Bank", or Barangay Names unless coverage is barangay-wide
        1c. Will not work if there is a name in the records (alphanumeric is considered a "duplicate")
    2. Online Application
        2a. Valid ID identification
            2a.1. Alphanumeric code of Valid ID
        2b. TIN (Taxpayer Identification Number)
            2a.1. If they have one, input the ID number
            2a.2. Else, notify them to get it at BIR before proceeding
        2c. DTI sends the e-certificate (display a message)
    3. Pay by any payment methods (GCash, Maya, credit/debit, or OTC)
        3a. Barangay - 230, City/Municipality - 530, Regional - 1030, National - 2030
            3a.1. +30 with documentary-stamp tax (DST) auto-collected
        3b. Notify the user to pay within 5 calendar days or the application lapses
    4. Download/print the Certificate of Business Name Registration
        4a. Notify the user to download multiple copies for Barangay, BIR, and Bank Opening
 */

public class FeeSchedule {

}
