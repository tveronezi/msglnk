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
import msglnk.service.{MailSessionService, BaseEAO}
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.ejb.embeddable.EJBContainer
import javax.inject.Inject
import java.util.Properties
import java.util.Scanner

class BaseEAOTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var baseEAO: BaseEAO = _
    @Inject var mailSessionService: MailSessionService = _
    private var id: Long = _
    private val sessionName = "default"

    @Before def setUp() {
        val p: Properties = new Properties
        p.put("movieDatabase", "new://Resource?type=DataSource")
        p.put("movieDatabase.JdbcDriver", "org.hsqldb.jdbcDriver")
        p.put("movieDatabase.JdbcUrl", "jdbc:hsqldb:mem:testdb")
        EJBContainer.createEJBContainer(p).getContext.bind("inject", this)
    }

    @Test def should_create_and_find_bean() {
        val bean: MailSession = new MailSession
        bean.setName(sessionName)
        bean.setConfig(new Scanner(classOf[BaseEAOTest].getResourceAsStream("")).useDelimiter("\\A").next)
        adminRunner.run({
            Any =>
                val s = baseEAO.create(bean)
                Assert.assertNotNull(s)
                id = s.getUid
        })
        adminRunner.run({
            Any =>
                val s = baseEAO.findById(classOf[MailSession], id).get
                Assert.assertNotNull(s)
                Assert.assertEquals(s.getUid, id)
        })
        adminRunner.run({
            Any =>
                val s = baseEAO.findUniqueBy(classOf[MailSession], "name", sessionName).get
                Assert.assertNotNull(s)
                Assert.assertEquals(s.getUid, id)
        })
        adminRunner.run({
            Any =>
                val s: Option[MailSession] = mailSessionService.getMailSessionByName(sessionName)
                s match {
                    case Some(session) =>
                    // expected
                    case None =>
                        Assert.fail("session not found")
                }
                Assert.assertNotNull(s)
                Assert.assertEquals(s.get.getUid, id)
        })
    }

    @Test def should_not_find_bean() {
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
}