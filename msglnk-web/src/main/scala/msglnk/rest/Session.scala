package msglnk.rest

import javax.ejb.Stateless
import javax.ws.rs.{POST, FormParam, Path, Produces}
import javax.inject.Inject
import msglnk.service.MailSessionService

@Path("/session")
@Produces(Array("application/json"))
@Stateless
class Session {

    @Inject var mailService: MailSessionService = _

    @POST
    def save(@FormParam("name") name: String, @FormParam("config") config: String) {
        mailService.saveSession(name, config)
    }

}
