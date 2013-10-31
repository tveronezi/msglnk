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

import msglnk.runners.{UnauthenticatedRunner, AdminRunner}
import org.junit.{Assert, Test}
import javax.inject.Inject
import msglnk.BaseTest

class RoleTest extends BaseTest {
    @Inject var adminRunner: AdminRunner = _
    @Inject var unRunner: UnauthenticatedRunner = _

    @Test
    def should_validate_role() {
        unRunner.run((r: UnauthenticatedRunner) => {
            Assert.assertFalse(r.ctx.isCallerInRole("solution-admin"))
        })
        adminRunner.run((r: AdminRunner) => {
            Assert.assertTrue(r.ctx.isCallerInRole("solution-admin"))
        })
    }
}