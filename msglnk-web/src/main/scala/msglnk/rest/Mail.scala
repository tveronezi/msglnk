package msglnk.rest

import javax.ejb.Stateless
import javax.ws.rs.{Consumes, POST, Path, Produces}
import msglnk.dto.EmailDto
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
            mailDto.getSession(),
            mailDto.getTo(),
            mailDto.getSubject(),
            mailDto.getText()
        )
    }

    @POST
    @Path("/trigger-read")
    def triggerRead() {
        mailService.readMailFromAllSessions()
    }
}
