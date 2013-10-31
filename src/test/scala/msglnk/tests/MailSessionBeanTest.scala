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
import msglnk.service.BaseEAO
import org.junit.Assert
import org.junit.Test
import javax.inject.Inject
import msglnk.BaseTest
import javax.ejb.{EJBTransactionRolledbackException, EJBException}

class MailSessionBeanTest extends BaseTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var baseEAO: BaseEAO = _

    private def createSessionObj(name: String, config: String) = {
        val bean: MailSession = new MailSession
        bean.setName(name)
        bean.setConfig(config)
        bean
    }

    @Test
    def name_should_be_unique() {
        try {
            adminRunner.run({
                baseEAO.create(createSessionObj("session a", "aaaaa"))
                baseEAO.create(createSessionObj("session a", "bbbbb"))
            })
            Assert.fail("Exception expected")
        }
        catch {
            case e: EJBException => {
                Assert.assertEquals(classOf[EJBTransactionRolledbackException], e.getCausedByException.getClass)
            }
            case _ => Assert.fail("EJBException expected")
        }
    }

    @Test
    def config_should_not_be_null() {
        adminRunner.run({
            baseEAO.create(createSessionObj("session not null", ""))
        })
        try {
            adminRunner.run({
                baseEAO.create(createSessionObj("session null", null))
            })
            Assert.fail("Exception expected")
        }
        catch {
            case e: EJBException => {
                Assert.assertEquals(classOf[EJBTransactionRolledbackException], e.getCausedByException.getClass)
            }
            case _ => Assert.fail("EJBException expected")
        }
    }

}