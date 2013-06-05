package msglnk.tests

import javax.ejb.Stateless
import javax.annotation.Resource
import javax.jms.{Queue, ConnectionFactory}

@Stateless
class AuxiliaryBean {
    @Resource var connectionFactory: ConnectionFactory = _
    @Resource(name = "SendEmailQueue") var sendMessageQueue: Queue = _
    @Resource(name = "IncomingEmailQueue") var newMessageQueue: Queue = _
}
