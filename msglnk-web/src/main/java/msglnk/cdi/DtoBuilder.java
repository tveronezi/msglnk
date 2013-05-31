package msglnk.cdi;

import msglnk.data.dto.EmailDto;
import msglnk.data.dto.EmailLogDto;
import msglnk.data.dto.MailSessionDto;
import msglnk.data.entity.Email;
import msglnk.data.entity.MailSession;
import msglnk.data.entity.MailSessionLog;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class DtoBuilder {

    public MailSessionDto build(MailSession mailSession) {
        final MailSessionDto dto = new MailSessionDto();
        dto.setName(mailSession.getName());
        dto.setAccount(mailSession.getAccount());
        return dto;
    }

    public List<MailSessionDto> buildSessions(Collection<MailSession> mailSessions) {
        final List<MailSessionDto> dtos = new ArrayList<MailSessionDto>();
        if (mailSessions != null) {
            for (MailSession mailSession : mailSessions) {
                dtos.add(build(mailSession));
            }
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
        if (emails != null) {
            for (Email email : emails) {
                dtos.add(build(email));
            }
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
        if (logs != null) {
            for (MailSessionLog log : logs) {
                dtos.add(build(log));
            }
        }
        return dtos;
    }

}
