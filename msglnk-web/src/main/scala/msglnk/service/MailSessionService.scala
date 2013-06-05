package msglnk.service

import javax.ejb.{EJB, Stateless}
import javax.annotation.security.RolesAllowed
import org.slf4j.{LoggerFactory, Logger}
import javax.annotation.Resource
import javax.jms._
import msglnk.data.MailSession
import msglnk.service.exception.MailSessionNotFound
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail._
import java.util.Properties
import java.io.{ByteArrayOutputStream, StringReader}
import javax.mail.Session
import javax.mail.Message
import scala.Some

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

    def readMail(sessionName: String) {
        getMailSessionByName(sessionName) match {
            case Some(mailSession) => {
                LOG.info("Reading emails from session '{}'", mailSession.getName)
                val session = getSession(mailSession)
                val store = session.getStore
                store.connect()

                try {
                    val folder = store.getDefaultFolder.getFolder("INBOX")
                    try {
                        folder.open(Folder.READ_WRITE)
                        val messages = folder.getMessages

                        // Use a suitable FetchProfile
                        val fp = new FetchProfile()
                        fp.add(FetchProfile.Item.ENVELOPE)
                        fp.add(FetchProfile.Item.FLAGS)
                        fp.add(FetchProfile.Item.CONTENT_INFO)
                        fp.add("X-Mailer")
                        folder.fetch(messages, fp)

                        LOG.info("{} new message(s) found.", messages.length)

                        for (message <- messages) {
                            try {
                                notifyNewEmail(mailSession, message)
                            }
                            catch {
                                case e: Exception => {
                                    LOG.error("Unable to read the email", e)
                                }
                            }
                            message.setFlag(Flags.Flag.DELETED, true)
                        }

                    } finally {
                        if (folder.isOpen) {
                            folder.close(true)
                        }
                    }

                } finally {
                    if (store.isConnected) {
                        store.close()
                    }
                }
            }
        }
    }

    def getMessageContent(message: Message): String = {
        message.getContent match {
            case s: String => s
            case p: Part => {
                val out = new ByteArrayOutputStream()
                p.writeTo(out)
                val contentType = message.getContentType
                if (contentType.contains("ISO-8859-1")) {
                    out.toString("ISO-8859-1")
                } else if (contentType.contains("UTF-8")) {
                    out.toString("UTF-8")
                } else {
                    out.toString
                }
            }
            case mp: Multipart => {
                LOG.warn("This application does not know how to read Multipart messages (yet)")
                ""
            }
            case other => {
                LOG.warn("This application does not know how to read this type of message ({})", other.getClass.getName)
                ""
            }
        }
    }

    def notifyNewEmail(mailSession: MailSession, message: Message) {
        val from = message.getFrom()(0).asInstanceOf[InternetAddress].getAddress
        val recipients = message.getRecipients(Message.RecipientType.TO)
        val date = message.getSentDate
        val content = getMessageContent(message)


        var connection: Connection = null
        var session: javax.jms.Session = null


        try {
            connection = this.factory.createConnection()
            connection.start()

            // Create a Session
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE)

            // Create a MessageProducer from the Session to the Topic or Queue
            val producer = session.createProducer(this.newEmailQueue)
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT)


            for (recipient <- recipients) {
                val to = recipient.asInstanceOf[InternetAddress].getAddress

                LOG.info("Notifying new email arrival. Session: {}; From: {}; To: {}, Date: {}; Content: {}.",
                    mailSession.getName, from, to, date, content)

                // Create a message
                val notification = session.createTextMessage(content)
                notification.setStringProperty("session", mailSession.getName)
                notification.setStringProperty("from", from)
                notification.setStringProperty("to", to)
                notification.setStringProperty("subject", message.getSubject)

                // Tell the producer to send the message
                producer.send(notification)

            }

        } finally {
            // Clean up
            if (session != null) {
                session.close()
            }
            if (connection != null) {
                connection.close()
            }
        }


    }
}
