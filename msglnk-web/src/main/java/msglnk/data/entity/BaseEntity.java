package msglnk.data.entity;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public boolean equals(Object o) {
        if (DefaultGroovyMethods.is(this, o)) return true;
        if (!getClass().equals(o.getClass())) return false;

        BaseEntity that = (BaseEntity) o;

        if (uid != that.uid) return false;

        return true;
    }

    public int hashCode() {
        return (uid != null ? uid.hashCode() : 0);
    }
}
