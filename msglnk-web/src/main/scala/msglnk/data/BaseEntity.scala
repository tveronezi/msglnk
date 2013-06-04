package msglnk.data

import javax.persistence.{Id, GeneratedValue, MappedSuperclass}
import java.lang.Long
import scala.reflect.BeanProperty

@MappedSuperclass
class BaseEntity {
    @Id
    @GeneratedValue
    @BeanProperty
    var uid: Long = _

    override def equals(that: Any): Boolean = {
        that.isInstanceOf[BaseEntity] && (this.hashCode() == that.asInstanceOf[BaseEntity].hashCode())
    }

    override def hashCode: Int = {
        if (uid == null) 0 else uid.hashCode
    }
}
