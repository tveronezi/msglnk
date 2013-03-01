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

package msglnk

import junit.framework.Assert
import msglnk.data.entity.Email
import msglnk.data.entity.MailSession
import msglnk.service.bean.MailImpl
import org.junit.Before
import org.junit.Test

import javax.ejb.EJB
import javax.ejb.embeddable.EJBContainer
import javax.naming.Context
import javax.naming.InitialContext
import javax.naming.NamingException
import java.util.concurrent.Callable

class TestCreateSession {

    @EJB
    private MailImpl mail

    private Context getContext(String group) throws NamingException {
        def p = [
                (Context.INITIAL_CONTEXT_FACTORY): 'org.apache.openejb.core.LocalInitialContextFactory',
                ('openejb.authentication.realmName'): 'ScriptLogin',
                (Context.SECURITY_PRINCIPAL): group,
                (Context.SECURITY_CREDENTIALS): ''
        ] as Properties

        return new InitialContext(p);
    }

    @Before
    public void setUp() throws Exception {
        def ctxCl = Thread.currentThread().getContextClassLoader()
        System.setProperty('openejb.ScriptLoginModule.scriptURI', ctxCl.getResource('loginscript.js').toExternalForm())

        def p = [:] as Properties
        EJBContainer.createEJBContainer(p).context.bind("inject", this)
    }

    @Test
    void testCreateSession() {
        final Context context = getContext("solution-admin");

        try {
            String sessionName = 'mySession'
            String user = 'usr@no-spam.org'
            String pass = 'no-spam'

            this.mail.persistSession(sessionName, user, pass, [
                    ('mail.smtp.auth'): 'true',
                    ('mail.smtp.starttls.enable'): 'true',
                    ('mail.smtp.host'): 'no-spam.org',
                    ('mail.smtp.port'): '587',
                    ('mail.debug'): 'true'
            ] as Map<String, String>)

            List<MailSession> sessions = this.mail.getSessions()
            Assert.assertNotNull(sessions)
            Assert.assertTrue(sessions.size() == 1)

            MailSession session = sessions.get(0)
            Assert.assertEquals(user, session.account)
            Assert.assertEquals(pass, session.password)

            Email email = this.mail.sendMail(
                    session.name,
                    'user@no-spam.org',
                    user,
                    'junit...', '... test')
            Assert.assertNotNull(email)
            System.out.print(email)
        } finally {
            context.close();
        }
    }
}
