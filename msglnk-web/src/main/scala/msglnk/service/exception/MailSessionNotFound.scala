package msglnk.service.exception

class MailSessionNotFound(sessionName: String) extends RuntimeException("Session '%s' not found".format(sessionName)) {

}
