package msglnk.runners

import javax.annotation.security.RunAs
import javax.ejb.Stateless

@Stateless
@RunAs("solution-admin") class AdminRunner {
    def run(callback: Any => Unit) {
        callback()
    }
}