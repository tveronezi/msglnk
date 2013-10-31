package msglnk.runners

import javax.annotation.Resource
import javax.jms.{Queue, ConnectionFactory}
import javax.ejb.SessionContext

class CommonAttributes {
    @Resource var ctx: SessionContext = _
    @Resource var connectionFactory: ConnectionFactory = _
    @Resource(name = "SendEmailQueue") var sendMessageQueue: Queue = _
    @Resource(name = "IncomingEmailQueue") var newMessageQueue: Queue = _
}
