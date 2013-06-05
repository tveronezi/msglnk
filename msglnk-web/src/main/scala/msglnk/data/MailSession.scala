package msglnk.data

import scala.reflect.BeanProperty
import javax.persistence._

@Entity
@Table(uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("session_name"))))
class MailSession extends BaseEntity {

    @Column(name = "session_name")
    @BeanProperty
    var name: String = _

    @Column(nullable = false)
    @Lob
    @BeanProperty
    var config: String = _

}
