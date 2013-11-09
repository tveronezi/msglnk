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

import javax.ejb.Stateless
import javax.ws.rs._
import javax.inject.Inject
import msglnk.service.{MailReaderTimers, MailSessionService}
import msglnk.dto.EmailSessionDto
import scala.Some
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

@Path("/session")
@Produces(Array("application/json"))
@Stateless
class Session {

    @Inject var mailService: MailSessionService = _

    @Inject var timers: MailReaderTimers = _

    @GET
    @Produces(Array("application/json"))
    def getSessions = {
        var sessions = new ListBuffer[EmailSessionDto]
        mailService.listSessions.foreach {
            session => {
                val dto = new EmailSessionDto()
                dto.setId(session.getUid)
                dto.setName(session.getName)
                dto.setNextRead(timers.getNextTimeout(session.getName) match {
                    case Some(timeout) => timeout
                    case None => -1l
                })
                sessions += dto
            }
        }
        sessions.asJava
    }

    @DELETE
    @Path("/{id}")
    @Produces(Array("application/json"))
    def deleteSession(@PathParam("id") id: Long) = {
        mailService.removeSession(id)
        true
    }

    @POST
    def save(@FormParam("name") name: String,
             @FormParam("user") user: String,
             @FormParam("password") password: String,
             @FormParam("config") config: String) {
        mailService.saveSession(name, user, password, config)
    }

}
