package msglnk.data.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mail_log_tbl")
public class MailSessionLog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "email_id", nullable = true)
    private Email email;

    @Lob
    @Column(name = "session_log", nullable = true)
    private String log;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_time", nullable = false)
    private Date date;

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
