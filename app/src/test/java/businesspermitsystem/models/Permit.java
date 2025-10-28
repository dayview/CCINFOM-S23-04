package businesspermitsystem.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a Permit in the Business Permit System
 * Contains permit issuance, validity, and status information
 */
public class Permit {
    private Integer permitId;
    private Integer businessId;
    private Integer permitTypeId;
    private String permitNo;
    private LocalDate issueDate;
    private LocalDate validityStart;
    private LocalDate validityEnd;
    private String status;
    private LocalDate statusEffectiveDate;
    private String note;

    public permit() {

    }

    public Permit(Integer permitId, Integer businessId, Integer permitTypeId, String permitNo,
                  LocalDate issueDate, LocalDate validityStart, LocalDate validityEnd,
                  String status, LocalDate statusEffectiveDate, String note) {
        this.permitId = permitId;
        this.businessId = businessId;
        this.permitTypeId = permitTypeId;
        this.permitNo = permitNo;
        this.issueDate = issueDate;
        this.validityStart = validityStart;
        this.validityEnd = validityEnd;
        this.status = status;
        this.statusEffectiveDate = statusEffectiveDate;
        this.note = note;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public Integer getBusinessId() {
        return businessId;
    }
}
