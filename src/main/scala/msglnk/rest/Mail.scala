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
import msglnk.dto.{NewEmailsDto, EmailDto}
import msglnk.service.MailSessionService
import javax.inject.Inject

@Path("/email")
@Produces(Array("application/json"))
@Stateless
class Mail {

    @Inject var mailService: MailSessionService = _

    @POST
    @Consumes(Array("application/json"))
    def send(mailDto: EmailDto) {
        mailService.sendMail(
            mailDto.getSessionId,
            mailDto.getTo,
            mailDto.getSubject,
            mailDto.getText
        )
    }

}
