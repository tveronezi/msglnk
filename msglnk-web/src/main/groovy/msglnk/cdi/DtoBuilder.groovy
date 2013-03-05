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

package msglnk.cdi

import msglnk.data.dto.EmailDto
import msglnk.data.dto.EmailLogDto
import msglnk.data.dto.MailSessionDto
import msglnk.data.entity.Email
import msglnk.data.entity.MailSession
import msglnk.data.entity.MailSessionLog

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class DtoBuilder {

    MailSessionDto build(MailSession mailSession) {
        return new MailSessionDto(
                name: mailSession.name,
                account: mailSession.account
        )
    }

    List<MailSessionDto> buildSessions(Collection<MailSession> mailSessions) {
        final List<MailSessionDto> dtos = new ArrayList<MailSessionDto>()
        mailSessions?.each {
            dtos.add(build(it))
        }
        return dtos
    }

    EmailDto build(Email email) {
        return new EmailDto(
                session: email.session.name,
                from: email.from.address,
                to: email.to.toString(),
                subject: email.subject,
                text: email.text
        )
    }

    List<EmailDto> buildEmails(Collection<Email> emails) {
        final List<EmailDto> dtos = new ArrayList<EmailDto>()
        emails?.each {
            dtos.add(build(it))
        }
        return dtos
    }

    EmailLogDto build(MailSessionLog log) {
        return new EmailLogDto(
                logId: log.uid,
                emailId: log.email.uid,
                log: log.log,
                timestamp: log.date.time
        )
    }

    List<EmailLogDto> buildLogs(Collection<MailSessionLog> logs) {
        final List<EmailLogDto> dtos = new ArrayList<EmailLogDto>()
        logs?.each {
            dtos.add(build(it))
        }
        return dtos
    }
}
