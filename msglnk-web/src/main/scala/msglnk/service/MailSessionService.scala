/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
import java.io.StringReader
import javax.mail.{Session, Message}
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
        if (name == null) {
            baseEAO.findUniqueBy(classOf[MailSession], "name", "default")
        } else {
            baseEAO.findUniqueBy(classOf[MailSession], "name", name)
        }
    }

    def saveSession(name: String, config: String): MailSession = {
        val session = {
            getMailSessionByName(name) match {
                case Some(existing) => existing
                case None => {
                    val newSession = new MailSession
                    newSession.setName(name)
                    newSession
                }
            }
        }
        session.setConfig(config)
        baseEAO.create(session)
    }

    private def loadProperties(content: String) = {
        val properties = new Properties()
        properties.load(new StringReader(content))
        properties
    }

    private def getSession(mailSession: MailSession): Session = {
        val properties = loadProperties(mailSession.getConfig)
        val user = properties.getProperty("ux_session_user_account")
        val password = properties.getProperty("ux_session_user_password")
        Session.getInstance(properties, new Authenticator() {
            override def getPasswordAuthentication: PasswordAuthentication = {
                new PasswordAuthentication(user, password)
            }
        })
    }

    def sendMail(sessionName: String, to: String, subject: String, text: String) {
        getMailSessionByName(sessionName) match {
            case Some(mailSession) => {
                val sessionProperties = loadProperties(mailSession.getConfig)
                val from = sessionProperties.getProperty("ux_session_user_account")

                LOG.info("Sending email. Session: {}; From: {}, To: {}, Subject: {}, Text: '{}'",
                    sessionName, from, to, subject, text)

                val message = new MimeMessage(getSession(mailSession))
                message.setFrom(new InternetAddress(from))
                message.setRecipients(Message.RecipientType.TO, to)
                message.setSubject(subject)
                message.setText(text)
                Transport.send(message)
            }
            case None => throw new MailSessionNotFound(sessionName)
        }
    }

    def readMailFromAllSessions() {
        val sessions = baseEAO.findAll(classOf[MailSession])
        for (session <- sessions) {
            try {
                readMail(session.getName)
            }
            catch {
                case e: Exception => LOG.error("Impossible to read email", e)
            }
        }
    }

    private def readMail(sessionName: String) {
        getMailSessionByName(sessionName) match {
            case Some(mailSession) => {
                LOG.info("Reading emails from session '{}'", mailSession.getName)
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
            case None => {
                LOG.warn("Impossible to read message from '{}'. Session not found.", sessionName)
            }
        }
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
