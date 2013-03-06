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

package msglnk.service.bean;

import msglnk.ApplicationException;
import msglnk.data.entity.*;
import msglnk.data.execution.BaseEAO;
import msglnk.data.execution.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Stateless(name = "msglnk-MailImpl")
@RolesAllowed({"solution-admin"})
public class MailImpl {
    private static final Logger LOG = LoggerFactory.getLogger(MailImpl.class);

    @Resource
    private ConnectionFactory factory;

    @Resource(name = "IncomingEmailQueue")
    private Queue newEmailQueue;

    @EJB
    private BaseEAO baseEAO;

    private class UserAuthenticator extends Authenticator {
        private final String user;
        private final String password;

        private UserAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    }

    private Session getSession(MailSession mailSession) {
        final Properties properties = new Properties();
        final List<SessionParameter> parameters = mailSession.getParameters();
        for (SessionParameter parameter : parameters) {
            properties.put(parameter.getKey(), parameter.getValue());
        }

        final String user = mailSession.getAccount();
        final String password = mailSession.getPassword();
        return Session.getInstance(properties, new UserAuthenticator(user, password));
    }

    public Email sendMail(String sessionName, String from, String to, String subject, String text) throws ApplicationException {
        final MailSession mailSession = this.baseEAO.execute(
                new FindByStringField<MailSession>(MailSession.class, "name", sessionName)
        );
        if (mailSession == null) {
            throw new ApplicationException("Session not found. Session name: " + sessionName);
        }

        final CreateEmail createEmail = new CreateEmail();
        createEmail.session = mailSession;

        if (from == null || "".equals(from.trim())) {
            createEmail.from = mailSession.getAccount();
        } else {
            createEmail.from = from.trim();
        }

        createEmail.to.add(to); //For now, only one recipient is supported
        createEmail.subject = subject;
        createEmail.text = text;
        createEmail.emailType = EmailType.OUTBOUND;
        final Email email = this.baseEAO.execute(createEmail);

        final CreateSessionLog createSessionLog = new CreateSessionLog();
        createSessionLog.email = email;
        try {
            final Message message = new MimeMessage(getSession(mailSession));
            message.setFrom(new InternetAddress(email.getFrom().getAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            createSessionLog.exception = e;
        }
        this.baseEAO.execute(createSessionLog);

        return email;
    }

    @Asynchronous
    public void sendMailAsynchronously(String sessionName, String from, String to, String subject, String text) {
        try {
            this.sendMail(sessionName, from, to, subject, text);
        } catch (ApplicationException e) {
            LOG.error("Unable to send the email", e);
        }
    }

    public MailSession persistSession(Properties config) {
        final String name = config.getProperty("ux_session_name", "default");
        String account = null;
        String password = null;
        final Map<String, String> parameters = new HashMap<String, String>();
        for (String key : config.stringPropertyNames()) {
            if ("ux_session_user_account".equals(key)) {
                account = config.getProperty(key);
            } else if ("ux_session_user_password".equals(key)) {
                password = config.getProperty(key);
            } else {
                parameters.put(key, config.getProperty(key));
            }
        }

        MailSession mailSession = this.baseEAO.execute(
                new FindByStringField<MailSession>(MailSession.class, "name", name)
        );
        if (mailSession == null) {
            final CreateSession createSession = new CreateSession();
            createSession.name = name;
            createSession.account = account;
            createSession.password = password;
            createSession.parametersMap = parameters;
            mailSession = this.baseEAO.execute(createSession);
        } else {
            mailSession.setAccount(account);
            mailSession.setPassword(password);

            final UpdateSession updateSession = new UpdateSession(mailSession, parameters);
            mailSession = this.baseEAO.execute(updateSession);
        }

        return mailSession;
    }

    public List<MailSession> getSessions() {
        return this.baseEAO.findAll(MailSession.class);
    }

    private void readEmails(MailSession mailSession) throws MessagingException, IOException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Reading emails from session " + mailSession);
        }

        final Session session = getSession(mailSession);
        final Store store = session.getStore();
        store.connect();

        try {
            final Folder folder = store.getDefaultFolder().getFolder("INBOX");
            try {
                folder.open(Folder.READ_WRITE);
                final Message[] messages = folder.getMessages();

                // Use a suitable FetchProfile
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(FetchProfile.Item.FLAGS);
                fp.add(FetchProfile.Item.CONTENT_INFO);
                fp.add("X-Mailer");
                folder.fetch(messages, fp);

                if (LOG.isInfoEnabled()) {
                    LOG.info(messages.length + " new message(s) found.");
                }

                for (Message message : messages) {
                    try {
                        read(mailSession, message);
                    } catch (Exception e) {
                        LOG.error("Unable to read the email", e);
                    }
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            } finally {
                if (folder.isOpen()) {
                    folder.close(true);
                }
            }

        } finally {
            if (store.isConnected()) {
                store.close();
            }
        }
    }

    private Email read(MailSession mailSession, Message message) throws IOException, MessagingException {
        final Object contentObj = message.getContent();

        final CreateEmail createEmail = new CreateEmail();
        createEmail.session = mailSession;

        final Address fromAddress = message.getFrom()[0];
        createEmail.from = ((InternetAddress) fromAddress).getAddress();

        final Address[] recipients = message.getRecipients(Message.RecipientType.TO);
        for (Address recipientAddress : recipients) {
            createEmail.to.add(recipientAddress.toString());
        }

        createEmail.subject = message.getSubject();

        final String contentType = message.getContentType();

        try {
            if (String.class.isInstance(contentObj)) {
                createEmail.text = contentObj.toString();
            } else {
                final Part contentPart = Part.class.cast(contentObj);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                contentPart.writeTo(out);

                if (contentType.contains("ISO-8859-1")) {
                    createEmail.text = out.toString("ISO-8859-1");
                } else if (contentType.contains("UTF-8")) {
                    createEmail.text = out.toString("UTF-8");
                } else {
                    createEmail.text = out.toString();
                }
            }
        } catch (Exception e) {
            createEmail.text = "";
            LOG.error("Impossible to get message text.", e);
        }

        createEmail.emailType = EmailType.INBOUND;
        final Email email = this.baseEAO.execute(createEmail);
        try {
            notifyNewEmail(email);
        } catch (JMSException e) {
            LOG.error("Unable to notify new email");
        }
        email.setNotified(Boolean.TRUE);
        return email;
    }

    private void notifyNewEmail(Email email) throws JMSException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Notifying new email arrival. Email: " + email);
        }

        Connection connection = null;
        javax.jms.Session session = null;

        try {
            connection = this.factory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(this.newEmailQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a message
            TextMessage message = session.createTextMessage(email.getText());
            message.setStringProperty("session", email.getSession().getName());
            message.setStringProperty("from", email.getFrom().getAddress());
            message.setStringProperty("to", email.getTo().get(0).getAddress());
            message.setStringProperty("subject", email.getSubject());

            // Tell the producer to send the message
            producer.send(message);
        } finally {
            // Clean up
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Asynchronous
    public void readEmails() {
        final List<MailSession> sessions = getSessions();

        if (LOG.isInfoEnabled()) {
            LOG.info("Reading emails from " + sessions.size() + " email(s) session(s).");
        }

        for (MailSession session : sessions) {
            try {
                readEmails(session);
            } catch (Exception e) {
                LOG.error("Something wrong while reading the emails", e);
            }
        }
    }

    public List<Email> getEmails() {
        return this.baseEAO.findAll(Email.class);
    }

    public List<MailSessionLog> getLogs() {
        return this.baseEAO.findAll(MailSessionLog.class);
    }
}
