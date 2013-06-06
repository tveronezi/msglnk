package msglnk.dto

import javax.xml.bind.annotation.{XmlElement, XmlAccessorType, XmlAccessType, XmlRootElement}
import scala.Predef.String
import scala.reflect.BeanProperty

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
class EmailDto {

    @XmlElement
    @BeanProperty
    var session: String = _

    @XmlElement
    @BeanProperty
    var from: String = _

    @XmlElement
    @BeanProperty
    var to: String = _

    @XmlElement
    @BeanProperty
    var subject: String = _

    @XmlElement
    @BeanProperty
    var text: String = _

}
