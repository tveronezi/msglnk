package msglnk.service

import javax.ejb.{EJB, Stateless}
import javax.annotation.security.RolesAllowed
import org.slf4j.{LoggerFactory, Logger}
import javax.annotation.Resource
import javax.jms.{Queue, ConnectionFactory}
import msglnk.data.MailSession

@Stateless
@RolesAllowed(Array("solution-admin"))
class MailSessionService {

    val LOG: Logger = LoggerFactory.getLogger(classOf[MailSessionService])

    @Resource
    var factory: ConnectionFactory = _

    @Resource(name = "IncomingEmailQueue")
    var newEmailQueue: Queue = _

    @EJB
    var baseEAO: BaseEAO = _

    def getMailSessionByName(name: String): Option[MailSession] = {
        baseEAO.findUniqueBy(classOf[MailSession], "name", name)
    }

}
