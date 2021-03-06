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
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletRequest
import java.security.Principal
import msglnk.dto.SessionDataDto

@Path("/keep-alive")
class KeepAlive {

    @GET
    def ping(@Context request: HttpServletRequest): SessionDataDto = {
        val session = request.getSession
        val dto = new SessionDataDto()
        dto.setSessionId(session.getId)
        request.getUserPrincipal match {
            case principal: Principal => {
                dto.setUserName(principal.getName)
                dto.setLogged(true)
            }
            case null => {
                dto.setUserName("guest")
                dto.setLogged(false)
            }
        }
        dto
    }

}
