package businesspermitsystem.models;

import java.sql.Timestamp;

public class NotificationLogModel {
    private int noticeId;
    private int businessId;
    private int ownerId;
    private String channel;
    private Timestamp sentDateTime;
    private String subject;
    private String messagePreview;

    public NotificationLogModel() {
    }

    /**
     * Constructor without noticeId (for INSERT operations)
     * Database will auto-generate the noticeId
     *
     * @param businessId the business this notification is about
     * @param ownerId the owner receiving the notification
     * @param channel communication method (SMS, email)
     * @param sentDateTime when the notification was sent
     * @param subject notification subject/title
     * @param messagePreview message content or preview
     */
    public NotificationLogModel(int businessId, int ownerId, String channel,
                                Timestamp sentDateTime, String subject,
                                String messagePreview) {
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.channel = channel;
        this.sentDateTime = sentDateTime;
        this.subject = subject;
        this.messagePreview = messagePreview;
    }

    /**
     * Full constructor with noticeId (for SELECT operations)
     *
     * @param noticeId unique notification ID
     * @param businessId the business this notification is about
     * @param ownerId the owner receiving the notification
     * @param channel communication method (SMS, email)
     * @param sentDateTime when the notification was sent
     * @param subject notification subject/title
     * @param messagePreview message content or preview
     */
    public NotificationLogModel(int noticeId, int businessId, int ownerId,
                                String channel, Timestamp sentDateTime,
                                String subject, String messagePreview) {
        this.noticeId = noticeId;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.channel = channel;
        this.sentDateTime = sentDateTime;
        this.subject = subject;
        this.messagePreview = messagePreview;
    }

    /**
     * @return the notification ID
     */
    public int getNoticeId() {
        return noticeId;
    }

    /**
     * @param noticeId sets the notification ID
     */
    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    /**
     * @return the business ID
     */
    public int getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId sets the business ID
     */
    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    /**
     * @return the owner ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId sets the owner ID
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the communication channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @param channel sets the communication channel
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * @return the sent timestamp
     */
    public Timestamp getSentDateTime() {
        return sentDateTime;
    }

    /**
     * @param sentDateTime sets the sent timestamp
     */
    public void setSentDateTime(Timestamp sentDateTime) {
        this.sentDateTime = sentDateTime;
    }

    /**
     * @return the notification subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject sets the notification subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the message preview
     */
    public String getMessagePreview() {
        return messagePreview;
    }

    /**
     * @param messagePreview sets the message preview
     *
     */
    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    @Override
    public String toString() {
        return "NotificationLog[" +
               "id=" + noticeId +
               ", businessId=" + businessId +
               ", ownerId=" + ownerId +
               ", channel=" + channel +
               ", sent=" + sentDateTime +
               "]";
    }
}