package businesspermitsystem.models;

/**
 * This model is for the owner data from the database
 */
public class OwnerModel {
    private int ownerID;
    private String lastName;
    private String firstName;
    private String middleName;
    private String contactNo;
    private String email;
    private String govID_type;
    private String govID_no;
    private String tin;
    private String homeAddress;

    public OwnerModel(){

    }


    public OwnerModel (int ownerID, String lastName, String firstName, String middleName, String contactNo, String email, 
                    String govID_type, String govID_no, String tin, String homeAddress) {
        this.ownerID = ownerID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.contactNo = contactNo;
        this.email = email;
        this.govID_type = govID_type;
        this.govID_no = govID_no;
        this.tin = tin;
        this.homeAddress = homeAddress;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGovID_type() {
        return govID_type;
    }

    public void setGovID_type(String govID_type) {
        this.govID_type = govID_type;
    }

    public String getGovID_no() {
        return govID_no;
    }

    public void setGovID_no(String govID_no) {
        this.govID_no = govID_no;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}