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

package msglnk.rest

import javax.ws.rs._
import org.slf4j.{Logger, LoggerFactory}
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import java.security.Principal

@Path("/keep-alive")
class KeepAlive {

    val LOG: Logger = LoggerFactory.getLogger(classOf[KeepAlive])

    @GET
    def ping(@Context request: HttpServletRequest) {
        val session = request.getSession
        val userName = request.getUserPrincipal match {
            case principal: Principal => principal.getName
            case null => "guest"
        }
        LOG.info("'keepAlive' event triggered. sessionID: '{}' user: '{}'.", session.getId, userName, "")
    }

}
