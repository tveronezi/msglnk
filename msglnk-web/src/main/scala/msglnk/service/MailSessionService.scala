package msglnk.service

import javax.ejb.{EJB, Stateless}
import javax.annotation.security.RolesAllowed
import org.slf4j.{LoggerFactory, Logger}
import javax.annotation.Resource
import javax.jms.{Queue, ConnectionFactory}
import msglnk.data.MailSession
import msglnk.service.exception.MailSessionNotFound
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail._
import java.util.Properties
import java.io.StringReader

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

    def createSession(name: String, config: String): MailSession = {
        val session = new MailSession
        session.setName(name)
        session.setConfig(config)
        baseEAO.create(session)
    }

    private def getSession(mailSession: MailSession): Session = {
        val properties = new Properties()
        properties.load(new StringReader(mailSession.getConfig))
        val user = properties.getProperty("ux_session_user_account")
        val password = properties.getProperty("ux_session_user_password")
        Session.getInstance(properties, new Authenticator() {
            override def getPasswordAuthentication: PasswordAuthentication = {
                new PasswordAuthentication(user, password)
            }
        })
    }

    def sendMail(sessionName: String, from: String, to: String, subject: String, text: String) {
        val mSession = getMailSessionByName(sessionName)
        mSession match {
            case None => throw new MailSessionNotFound(sessionName)
        }
        LOG.info("Sending email. Session: {}; From: {}, To: {}, Subject: {}, Text: '{}'",
            sessionName, from, to, subject, text)

        val message = new MimeMessage(getSession(mSession.get))
        message.setFrom(new InternetAddress(from))
        message.setRecipients(Message.RecipientType.TO, to)
        message.setSubject(subject)
        message.setText(text)
        Transport.send(message)
    }

}
