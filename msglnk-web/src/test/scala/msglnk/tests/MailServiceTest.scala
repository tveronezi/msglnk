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
import java.io.File
import org.slf4j.{LoggerFactory, Logger}

@Stateless
class MailServiceTest extends BaseTest {
    val LOG: Logger = LoggerFactory.getLogger(classOf[MailServiceTest])

    @Inject var adminRunner: AdminRunner = _
    @Inject var mailSessionService: MailSessionService = _

    val sessionName = "MailServiceTest_SessionName"
    val url = getClass.getResource("/default-session.properties")
    val config = Source.fromURL(url).mkString

    val envKey = "MSGLNK_MAIL_SESSION_CONFIG"
    val configFile = {
        val testMailConfigPath = System.getenv(envKey)
        if (testMailConfigPath == null) {
            LOG.warn("The environment variable {} is not defined.", envKey)
            None
        } else {
            val file = new File(testMailConfigPath)
            if (file.exists()) {
                val content = Source.fromFile(file).mkString
                Option(content)
            } else {
                LOG.warn("Unable to find the file path {}.", file.getAbsolutePath)
                None
            }
        }
    }

    @Test
    def should_create_and_find_bean() {
        adminRunner.run({
            Any =>
                val session = mailSessionService.saveSession(sessionName, config)
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

    @Test
    def should_send_email() {
        configFile match {
            case Some(content) => {
                adminRunner.run({
                    Any =>
                        val testSessionName: String = "testSession"
                        mailSessionService.saveSession(testSessionName, content)
                        mailSessionService.sendMail(
                            testSessionName,
                            "test@veronezi.org",
                            "thiago@veronezi.org",
                            "unit test",
                            "is this working?")
                })
            }
            case None => {
                Assert.fail("No config file found. Check the system variable '%s' and try it again.".format(envKey))
            }
        }
    }

}