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
package msglnk.tests

import msglnk.data.MailSession
import msglnk.runners.AdminRunner
import msglnk.service.MailSessionService
import org.junit.{Assert, Test}
import javax.inject.Inject
import scala.io.Source
import javax.ejb.{EJBException, Stateless}
import msglnk.service.exception.MailSessionNotFound
import msglnk.BaseTest

@Stateless
class MailServiceTest extends BaseTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var mailSessionService: MailSessionService = _

    val sessionName = "MailServiceTest_SessionName"
    val url = getClass.getResource("/default-session.properties")
    val config = Source.fromURL(url)

    @Test
    def should_create_and_find_bean() {
        adminRunner.run({
            Any =>
                val session = mailSessionService.createSession(sessionName, config.mkString)
                Assert.assertNotNull(session)
                Assert.assertNotNull(session.getUid)
        })
        adminRunner.run({
            Any =>
                val s: Option[MailSession] = mailSessionService.getMailSessionByName(sessionName)
                s match {
                    case Some(session) =>
                        Assert.assertEquals(session.getName, sessionName)
                    case None =>
                        Assert.fail("session not found")
                }
                Assert.assertNotNull(s)
        })
    }

    @Test
    def should_not_find_bean() {
        adminRunner.run({
            Any =>
                val s: Option[MailSession] = mailSessionService.getMailSessionByName("fooSession")
                s match {
                    case Some(session) =>
                        Assert.fail("session found")
                    case None =>
                    // expected
                }
                Assert.assertNotNull(s)
        })
    }

    @Test
    def should_not_send_email() {
        adminRunner.run({
            Any =>
                try {
                    mailSessionService.sendMail("sessionFoo", "from", "to", "subject", "text")
                    Assert.fail("exception expected")
                }
                catch {
                    case ejbe: EJBException => {
                        if (!classOf[MailSessionNotFound].isInstance(ejbe.getCause)) {
                            Assert.fail("wrong exception (1)")
                        }
                    }
                    case e: Exception => {
                        Assert.fail("wrong exception (2)")
                    }
                }
        })
    }
}