/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msglnk.service

import javax.ejb._
import javax.inject.Inject
import javax.enterprise.event.Event
import msglnk.events.ReadMailEvent
import javax.annotation.Resource
import scala.Some
import msglnk.service.exception.InvalidParameterException
import javax.annotation.security.RunAs

@Singleton
@RunAs("solution-admin")
class MailReaderTimers {

    val readIntervalValue = 5 * 60 * 1000 //5 minutes

    @Inject
    var readEvent: Event[ReadMailEvent] = _

    @Resource
    var timerService: TimerService = _

    var readMailHandles = Map[String, TimerHandle]()

    private def addReadMailHandle(sessionName: String, handle: TimerHandle) {
        cancelHandle(sessionName)
        readMailHandles = readMailHandles + (sessionName -> handle)
    }

    @Lock(LockType.READ)
    def getNextTimeout(sessionName: String): Option[Long] = {
        readMailHandles.get(sessionName) match {
            case Some(handle) => {
                try {
                    Option(handle.getTimer.getNextTimeout.getTime)
                }
                catch {
                    case e: Exception => {
                        None
                    }
                }
            }
            case None => None
        }
    }

    def cancelHandle(sessionName: String) {
        readMailHandles.get(sessionName) match {
            case Some(handle) => {
                try {
                    handle.getTimer.cancel()
                }
                catch {
                    case e: Exception => {
                        // ignore
                    }
                }
            }
            case None => //ignore
        }
    }

    def getNextTimeouts: Map[String, Long] = {
        readMailHandles.mapValues(handle => {
            try {
                handle.getTimer.getNextTimeout.getTime
            }
            catch {
                case e: Exception => -1l
            }
        })
    }

    def scheduleSessionRead(sessionName: String, timeout: Int) {
        if (sessionName == null || "".equals(sessionName.trim)) {
            throw new InvalidParameterException("TimerConfig info should be the String 'sessionName'")
        }
        val timer = timerService.createSingleActionTimer(timeout, new TimerConfig(sessionName, true))
        addReadMailHandle(sessionName, timer.getHandle)
    }

    @Timeout
    def readMail(timer: Timer) {
        val sessionName = {
            timer.getInfo match {
                case name: String => name
            }
        }
        try {
            readEvent.fire(new ReadMailEvent(sessionName))
            scheduleSessionRead(sessionName, readIntervalValue)
        }
        catch {
            case e: Exception => {
                cancelHandle(sessionName)
                readMailHandles -= sessionName // removing handle
            }
        }
    }

}
