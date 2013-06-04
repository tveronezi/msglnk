package msglnk.data

import scala.reflect.BeanProperty
import javax.persistence._

@Entity
class MailSession extends BaseEntity {

    @BeanProperty
    var name: String = _

    @Column(nullable = false)
    @Lob
    @BeanProperty
    var config: String = _

}
