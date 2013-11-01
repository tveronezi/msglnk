/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package msglnk.mdb

import javax.ejb.{EJB, MessageDriven}
import org.slf4j.LoggerFactory
import javax.jms.{Message, MessageListener}
import msglnk.service.MailSessionService
import javax.annotation.security.RunAs

@MessageDriven(mappedName = "SendEmailQueue", messageListenerInterface = classOf[MessageListener])
@RunAs("solution-admin")
class SendEmailRequest extends MessageListener {
    val LOG = LoggerFactory.getLogger(classOf[SendEmailRequest])

    @EJB var mailSession: MailSessionService = _

    def onMessage(message: Message) {
        val sessionName = message.getStringProperty("sessionName")
        val to = message.getStringProperty("to")
        val subject = message.getStringProperty("subject")
        val text = message.getStringProperty("text")
        if(sessionName == null) {
            mailSession.sendMail("default", to, subject, text)
        } else {
            mailSession.sendMail(sessionName, to, subject, text)
        }

    }
}
