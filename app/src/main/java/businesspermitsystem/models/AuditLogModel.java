package businesspermitsystem.models;

import java.sql.Timestamp;

public class AuditLogModel {
    private int auditId;
    private String entity;
    private int entityId;
    private String action;
    private String changedByUser;
    private Timestamp changedDateTime;
    private String changeSummary;

    /**
     * Default constructor
     */
    public AuditLogModel() {
    }

    /**
     * Constructor without auditId (for INSERT operations)
     * Database will auto-generate the auditId
     *
     * @param entity the entity type (Business, Permit, etc.)
     * @param entityId the ID of the changed record
     * @param action the operation type (CREATE, UPDATE, DELETE)
     * @param changedByUser who made the change
     * @param changedDateTime when the change occurred
     * @param changeSummary description of what changed
     */
    public AuditLogModel(String entity, int entityId, String action,
                         String changedByUser, Timestamp changedDateTime,
                         String changeSummary) {
        this.entity = entity;
        this.entityId = entityId;
        this.action = action;
        this.changedByUser = changedByUser;
        this.changedDateTime = changedDateTime;
        this.changeSummary = changeSummary;
    }

    /**
     * Full constructor with auditId (for SELECT operations)
     *
     * @param auditId unique audit log ID
     * @param entity the entity type
     * @param entityId the ID of the changed record
     * @param action the operation type
     * @param changedByUser who made the change
     * @param changedDateTime when the change occurred
     * @param changeSummary description of what changed
     */
    public AuditLogModel(int auditId, String entity, int entityId, String action,
                         String changedByUser, Timestamp changedDateTime,
                         String changeSummary) {

    }

    /**
     * @return the audit log ID
     */
    public int getAuditId() {
        return auditId;
    }

    /**
     * @param auditId sets the audit log ID
     */
    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    /**
     * @return the entity type
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity sets the entity type
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the entity record ID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @param entityId sets the entity record ID
     */
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the action performed
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action sets the action performed
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the user who made the change
     */
    public String getChangedByUser() {
        return changedByUser;
    }

    /**
     * @param changedByUser sets the user who made the change
     */
    public void setChangedByUser(String changedByUser) {
        this.changedByUser = changedByUser;
    }

    /**
     * @return the timestamp of the change
     */
    public Timestamp getChangedDateTime() {
        return changedDateTime;
    }

    /**
     * @param changedDateTime sets the timestamp of the change
     */
    public void setChangedDateTime(Timestamp changedDateTime) {
        this.changedDateTime = changedDateTime;
    }

    /**
     * @return the summary of changes
     */
    public String getChangeSummary() {
        return changeSummary;
    }

    /**
     * @param changeSummary sets the summary of changes
     */
    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    /**
     * Returns a string representation of the audit log entry
     */
    @Override
    public String toString() {
        return "AuditLog[" +
                "id=" + auditId +
                ", entity=" + entity +
                ", entityId=" + entityId +
                ", action=" + action +
                ", user=" + changedByUser +
                ", time=" + changedDateTime +
                "]";
    }
}
