package msglnk

import javax.ws.rs.ApplicationPath
import java.util
import msglnk.rest.{Session, Mail}
import scala.collection.JavaConverters._

@ApplicationPath("/rest")
class ApplicationConfig extends javax.ws.rs.core.Application {
    override def getClasses: util.Set[Class[_]] = {
        Set[Class[_]](classOf[Mail], classOf[Session]).asJava
    }
}
