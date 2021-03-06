/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msglnk.service

import javax.ejb._
import javax.annotation.security.RolesAllowed
import org.slf4j.{LoggerFactory, Logger}
import javax.annotation.Resource
import javax.jms._
import msglnk.data.MailSession
import msglnk.exception.{InvalidParameterException, MailSessionIdNotFound}
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail._
import java.util.Properties
import java.io.StringReader
import javax.mail.{Session, Message}
import scala.Some
import javax.inject.Inject
import javax.enterprise.event.Observes
import msglnk.events.ReadMailEvent

@Stateless
@RolesAllowed(Array("solution-admin"))
class MailSessionService {

    val LOG: Logger = LoggerFactory.getLogger(classOf[MailSessionService])

    @Resource
    var factory: ConnectionFactory = _

    @Resource(name = "IncomingEmailQueue")
    var newEmailQueue: Queue = _

    @Inject
    var baseEAO: BaseEAO = _

    @Inject
    var connections: Connections = _

    @Inject
    var timersHolder: MailReaderTimers = _

    def listSessions = {
        baseEAO.findAll(classOf[MailSession])
    }

    def findSession(id: Long) = {
        baseEAO.findById(classOf[MailSession], id)
    }

    def removeSession(id: Long) {
        findSession(id) match {
            case Some(session) => timersHolder.cancelHandle(session.getUid)
            case None => // ignore
        }
        baseEAO.delete(classOf[MailSession], id)
    }

    def listenToReadEvent(@Observes evt: ReadMailEvent) {
        try {
            readMail(evt.sessionId)
        }
        catch {
            case e: MailSessionIdNotFound => throw e
            case e: Exception => LOG.error("Impossible to read email", e)
        }
    }

    def saveSession(id: Long, name: String, userName: String, userPassword: String, config: String): MailSession = {
        def setValues(session: MailSession) = {
            session.setName(name)
            session.setUserName(userName)
            session.setUserPassword(userPassword)
            session.setConfig(config)
            baseEAO.create(session)
        }
        findSession(id) match {
            case Some(existing) => setValues(existing)
            case None => {
                val newSession = setValues(new MailSession)
                timersHolder.scheduleSessionRead(newSession.getUid, 1000) // one second
                newSession
            }
        }
    }

    private def loadProperties(content: String) = {
        val properties = new Properties()
        properties.load(new StringReader(content))
        properties
    }

    private def getSession(mailSession: MailSession): Session = {
        val properties = loadProperties(mailSession.getConfig)
        Session.getInstance(properties, new Authenticator() {
            override def getPasswordAuthentication: PasswordAuthentication = {
                new PasswordAuthentication(mailSession.getUserName, mailSession.getUserPassword)
            }
        })
    }

    def sendMail(sessionId: Long, to: String, subject: String, text: String) {
        findSession(sessionId) match {
            case Some(mailSession) => {
                val from = mailSession.getUserName
                val message = new MimeMessage(getSession(mailSession))
                message.setFrom(new InternetAddress(from))
                message.setRecipients(Message.RecipientType.TO, to)
                message.setSubject(subject)
                message.setText(text)
                Transport.send(message)
                connections.sendToAll("email-sent", Map(
                    "sessionName" -> mailSession.getName,
                    "from" -> from,
                    "to" -> to,
                    "subject" -> subject,
                    "text" -> text
                ))
            }
            case None => throw new MailSessionIdNotFound(sessionId)
        }
    }

    def getMailSessionByName(name: String): Option[MailSession] = {
        if (name == null) {
            throw new InvalidParameterException("Session name cannot be null")
        }
        baseEAO.findUniqueBy(classOf[MailSession], "name", name.trim())
    }

    private def readMail(sessionId: Long): Int = {
        var number = 0
        findSession(sessionId) match {
            case Some(mailSession) => {
                connections.sendToAll("email-read-start", Map("sessionName" -> mailSession.getName))
                val session = getSession(mailSession)
                val store = session.getStore
                store.connect()

                try {
                    val folderName = {
                        val props = loadProperties(mailSession.getConfig)
                        if (props.contains("ux_session_folder")) {
                            props.getProperty("ux_session_folder")
                        } else {
                            "INBOX"
                        }
                    }
                    val folder = store.getDefaultFolder.getFolder(folderName)
                    try {
                        folder.open(Folder.READ_WRITE)
                        val messages = folder.getMessages
                        number = messages.size

                        // Use a suitable FetchProfile
                        val fp = new FetchProfile()
                        fp.add(FetchProfile.Item.ENVELOPE)
                        fp.add(FetchProfile.Item.FLAGS)
                        fp.add(FetchProfile.Item.CONTENT_INFO)
                        fp.add("X-Mailer")
                        folder.fetch(messages, fp)

                        connections.sendToAll("email-read-count",
                            Map("sessionName" -> mailSession.getName, "count" -> messages.length)
                        )

                        for (message <- messages) {
                            try {
                                notifyNewEmail(mailSession, message)
                            }
                            catch {
                                case e: Exception => {
                                    connections.sendToAll("email-read-error", Map())
                                    LOG.error("Unable to read the email", e)
                                }
                            } finally {
                                message.setFlag(Flags.Flag.DELETED, true)
                            }
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
            case None => throw new MailSessionIdNotFound(sessionId)
        }
        number
    }

    def getContentText(any: AnyRef): Option[String] = {
        any match {
            case s: String => Option(s)
            case p: Part => {
                Option(p.getContent.toString)
            }
            case mp: Multipart => {
                getMultipartContentText(0, mp)
            }
            case _ => None
        }
    }

    def getMultipartContentText(index: Int, mp: Multipart): Option[String] = {
        if (index >= mp.getCount) {
            None
        } else {
            getContentText(mp.getBodyPart(index)) match {
                case None => {
                    if (index + 1 < mp.getCount) {
                        getMultipartContentText(index + 1, mp)
                    } else {
                        None
                    }
                }
                case Some(text) => Option(text)
            }
        }
    }

    def getMessageContent(message: Message): Option[String] = {
        message.getContent match {
            case s: String => getContentText(s)
            case s: Part => getContentText(s)
            case s: Multipart => getContentText(s)
            case other => {
                LOG.warn("This application does not know how to read this type of message ({})", other.getClass.getName)
                None
            }
        }
    }

    private def notifyNewEmail(mailSession: MailSession, message: Message) {
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
                val notification = session.createMessage()
                notification.setStringProperty("session", mailSession.getName)
                notification.setStringProperty("from", from)
                notification.setStringProperty("to", to)
                notification.setStringProperty("subject", message.getSubject)
                notification.setStringProperty("content", {
                    content match {
                        case Some(text) => text
                        case None => ""
                    }
                })
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
