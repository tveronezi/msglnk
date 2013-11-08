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
import msglnk.service.MailReaderTimers
import msglnk.dto.NextSessionScheduledReadDto
import scala.collection.mutable
import scala.collection.JavaConverters._

@Path("/session-reader")
@Produces(Array("application/json"))
@Stateless
class SessionReader {

    @Inject var service: MailReaderTimers = _

    @GET
    @Path("/timeouts")
    def getTimeouts = {
        val result = new mutable.MutableList[NextSessionScheduledReadDto]()
        service.getNextTimeouts.foreach {
            case (name, timestamp) => {
                val dto = new NextSessionScheduledReadDto()
                dto.setName(name)
                dto.setTimestamp(timestamp)
                result += dto
            }
        }
        result.asJava
    }

    @GET
    @POST
    @Path("/read/{sessionName}")
    def readSession(@PathParam("sessionName") sessionName: String) {
        val result = new mutable.MutableList[NextSessionScheduledReadDto]()
        service.scheduleSessionRead(sessionName, 1000) // one second
    }

}