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

class BaseEAOTest extends BaseTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var baseEAO: BaseEAO = _

    private def createSessionObj(name: String, config: String) = {
        val bean: MailSession = new MailSession
        bean.setName(name)
        bean.setConfig(config)
        bean
    }

    @Test
    def should_create_and_find_bean() {
        val sessionName = System.currentTimeMillis().toString + "_test"
        adminRunner.run({
            val id = baseEAO.create(createSessionObj(sessionName, "aabbcc")).getUid
            baseEAO.findById(classOf[MailSession], id) match {
                case Some(s) => {
                    Assert.assertEquals(s.getUid, id)
                }
                case None => {
                    Assert.fail("Session not found")
                }
            }

            baseEAO.findUniqueBy(classOf[MailSession], "name", sessionName) match {
                case Some(s) => {
                    Assert.assertEquals(s.getUid, id)
                }
                case None => {
                    Assert.fail("Session not found")
                }
            }
        })
    }

    @Test
    def should_create_and_update_bean() {
        adminRunner.run({
            val testName = System.currentTimeMillis().toString + "_session"
            val dummyContentA = "ddeeff"
            baseEAO.create(createSessionObj(testName, dummyContentA))
            baseEAO.findUniqueBy(classOf[MailSession], "name", testName) match {
                case Some(mailSession) => {
                    Assert.assertEquals(mailSession.getConfig, dummyContentA)

                    val dummyContentB = "gghhii"
                    mailSession.setConfig(dummyContentB)
                    baseEAO.create(mailSession)
                    baseEAO.findUniqueBy(classOf[MailSession], "name", testName) match {
                        case Some(mailSession) => {
                            Assert.assertEquals(mailSession.getConfig, dummyContentB)
                        }
                        case None => {
                            Assert.fail("Session not found")
                        }
                    }
                }
                case None => {
                    Assert.fail("Session not found")
                }
            }
        })
    }

}