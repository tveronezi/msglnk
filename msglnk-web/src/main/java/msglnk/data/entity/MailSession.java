package msglnk.data.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mail_session_tbl", uniqueConstraints = @UniqueConstraint(columnNames = {"mail_session_name"}))
public class MailSession extends BaseEntity {

    @Column(name = "mail_session_name", nullable = false)
    private String name;

    @Column(name = "mail_user", nullable = false)
    private String account;

    @Column(name = "mail_password", nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mailSession")
    private List<SessionParameter> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SessionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SessionParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "MailSession{" +
                "name='" + name + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
