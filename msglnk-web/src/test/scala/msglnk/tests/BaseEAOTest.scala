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
import org.junit.Before
import org.junit.Test
import javax.ejb.embeddable.EJBContainer
import javax.inject.Inject
import java.util.Properties
import java.util.Scanner

class BaseEAOTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var baseEAO: BaseEAO = _
    private var id: Long = _


    @Before def setUp {
        val p: Properties = new Properties
        p.put("movieDatabase", "new://Resource?type=DataSource")
        p.put("movieDatabase.JdbcDriver", "org.hsqldb.jdbcDriver")
        p.put("movieDatabase.JdbcUrl", "jdbc:hsqldb:mem:testdb")
        EJBContainer.createEJBContainer(p).getContext.bind("inject", this)
    }

    @Test def should_create_and_find_bean {
        val bean: MailSession = new MailSession
        bean.setName("default")
        bean.setConfig(new Scanner(classOf[BaseEAOTest].getResourceAsStream("")).useDelimiter("\\A").next)
        adminRunner.run({ Any =>
            val s: MailSession = baseEAO.create(bean)
            Assert.assertNotNull(s)
            id = s.getUid
        })
        adminRunner.run({ Any =>
            val s: MailSession = baseEAO.findById(classOf[MailSession], id).get
            Assert.assertNotNull(s)
            Assert.assertEquals(s.getUid, id)
        })
    }
}