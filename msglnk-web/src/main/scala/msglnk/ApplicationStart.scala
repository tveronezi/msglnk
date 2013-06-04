package msglnk

import javax.annotation.security.RunAs
import javax.ejb.Startup
import javax.ejb.Singleton
import javax.annotation.PostConstruct
import org.slf4j.{LoggerFactory, Logger}

@Singleton(name = "MsglnkApplicationStart")
@Startup
@RunAs("solution-admin")
class ApplicationStart {
    val LOG: Logger = LoggerFactory.getLogger(classOf[ApplicationStart])

    @PostConstruct def applicationStartup {
        LOG.info("Starting MSGLNK...")
    }

}
