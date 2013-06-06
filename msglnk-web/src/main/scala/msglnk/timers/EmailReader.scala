package msglnk.timers

import javax.annotation.security.RunAs
import javax.ejb.{Schedule, Stateless}
import org.slf4j.LoggerFactory
import javax.inject.Inject
import msglnk.service.MailSessionService

@Stateless
@RunAs("solution-admin")
class EmailReader {

    val LOG = LoggerFactory.getLogger(classOf[EmailReader])

    @Inject var mail: MailSessionService = _

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    def readEmails() {
        LOG.info("Email reader triggered")
        mail.readMailFromAllSessions()
    }
}
