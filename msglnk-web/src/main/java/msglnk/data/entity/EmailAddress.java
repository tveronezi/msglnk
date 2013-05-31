package msglnk.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mail_address_tbl", uniqueConstraints = @UniqueConstraint(columnNames = {"mail_address"}))
public class EmailAddress extends BaseEntity {
    @Override
    public String toString() {
        return address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "mail_address", nullable = false)
    private String address;
}
