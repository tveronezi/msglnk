package msglnk.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "mail_param_tbl")
public class SessionParameter extends BaseEntity {

    @Column(name = "param_key", nullable = false)
    private String key;

    @Column(name = "param_value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private MailSession mailSession;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MailSession getMailSession() {
        return mailSession;
    }

    public void setMailSession(MailSession mailSession) {
        this.mailSession = mailSession;
    }
}
