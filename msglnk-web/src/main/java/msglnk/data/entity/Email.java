package msglnk.data.entity;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mail_tbl")
public class Email extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "msg_session_id", nullable = false, updatable = false)
    private MailSession session;

    @ManyToOne(optional = false)
    @JoinColumn(name = "msg_from_id", nullable = false, updatable = false)
    private EmailAddress from;

    @ManyToMany
    @JoinTable(name = "emails_recipients_tbl")
    private List<EmailAddress> to = new ArrayList<EmailAddress>();

    @Column(name = "msg_subject", nullable = false, updatable = false)
    private String subject;

    @Lob
    @Column(name = "msg_text", nullable = false, updatable = false)
    private String text;

    @Column(name = "msg_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    @Column(name = "msg_notified", nullable = false)
    private Boolean notified = Boolean.FALSE;

    public MailSession getSession() {
        return session;
    }

    public void setSession(MailSession session) {
        this.session = session;
    }

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public List<EmailAddress> getTo() {
        return to;
    }

    public void setTo(List<EmailAddress> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    @PrePersist
    public void normalize() {
        if (DefaultGroovyMethods.asBoolean(this.subject)) {
            this.subject = this.subject.trim();
        }

        if (DefaultGroovyMethods.asBoolean(this.text)) {
            this.text = this.text.trim();
        }

    }

    @Override
    public String toString() {
        return "Email{" +
                "session=" + session +
                ", from=" + from +
                ", to=" + to +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", emailType=" + emailType +
                ", notified=" + notified +
                '}';
    }
}
