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

package msglnk

import org.junit.{After, Before}
import javax.ejb.embeddable.EJBContainer
import java.util.Properties

class BaseTest {

    var container: EJBContainer = _

    @Before
    def setUp() {
        val p: Properties = new Properties
        p.put("movieDatabase", "new://Resource?type=DataSource")
        p.put("movieDatabase.JdbcDriver", "org.hsqldb.jdbcDriver")
        p.put("movieDatabase.JdbcUrl", "jdbc:hsqldb:mem:testdb")
        container = EJBContainer.createEJBContainer(p)
        container.getContext.bind("inject", this)
    }

    @After
    def tearDown() {
        container.close()
    }

}