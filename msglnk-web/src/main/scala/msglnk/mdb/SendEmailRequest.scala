package msglnk.mdb

import javax.ejb.MessageDriven
import org.slf4j.LoggerFactory
import javax.jms.{Message, MessageListener}
import javax.inject.Inject
import msglnk.service.MailSessionService
import javax.annotation.security.RunAs

@MessageDriven(mappedName = "SendEmailQueue", messageListenerInterface = classOf[MessageListener])
@RunAs("solution-admin")
class SendEmailRequest extends MessageListener {
    val LOG = LoggerFactory.getLogger(classOf[SendEmailRequest])

    @Inject var mailSession: MailSessionService = _

    def onMessage(message: Message) {
        val sessionName = message.getStringProperty("sessionName")
        val to = message.getStringProperty("to")
        val subject = message.getStringProperty("subject")
        val text = message.getStringProperty("text")
        mailSession.sendMail(sessionName, to, subject, text)
    }
}
