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

package msglnk.service.bean;

import msglnk.data.dto.EmailDto;
import msglnk.data.dto.EmailLogDto;
import msglnk.data.dto.MailSessionDto;
import msglnk.data.entity.Email;
import msglnk.data.entity.MailSession;
import msglnk.data.entity.MailSessionLog;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Stateless
public class DtoBuilderImpl {

    public MailSessionDto build(MailSession mailSession) {
        final MailSessionDto dto = new MailSessionDto();
        dto.setName(mailSession.getName());
        dto.setAccount(mailSession.getAccount());
        return dto;
    }

    public List<MailSessionDto> buildSessions(Collection<MailSession> mailSessions) {
        final List<MailSessionDto> dtos = new ArrayList<MailSessionDto>();
        for (MailSession mailSession : mailSessions) {
            dtos.add(build(mailSession));
        }
        return dtos;
    }

    public EmailDto build(Email email) {
        final EmailDto dto = new EmailDto();
        dto.setSession(email.getSession().getName());
        dto.setFrom(email.getFrom().getAddress());
        dto.setTo(email.getTo().toString());
        dto.setSubject(email.getSubject());
        dto.setText(email.getText());
        return dto;
    }

    public List<EmailDto> buildEmails(Collection<Email> emails) {
        final List<EmailDto> dtos = new ArrayList<EmailDto>();
        for (Email email : emails) {
            dtos.add(build(email));
        }
        return dtos;
    }

    public EmailLogDto build(MailSessionLog log) {
        final EmailLogDto dto = new EmailLogDto();
        dto.setLogId(log.getUid());
        dto.setEmailId(log.getEmail().getUid());
        dto.setLog(log.getLog());
        dto.setTimestamp(log.getDate().getTime());
        return dto;
    }

    public List<EmailLogDto> buildLogs(Collection<MailSessionLog> logs) {
        final List<EmailLogDto> dtos = new ArrayList<EmailLogDto>();
        for (MailSessionLog log : logs) {
            dtos.add(build(log));
        }
        return dtos;
    }
}
