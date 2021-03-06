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

import javax.websocket.Session
import javax.ejb.{LockType, Lock}
import com.google.gson.Gson
import scala.collection.JavaConverters._

@javax.ejb.Singleton
class Connections {

    val gson = new Gson()
    var sessions: Set[Session] = Set()

    @Lock(LockType.WRITE)
    def addSession(session: Session) {
        sessions = sessions + session
    }

    @Lock(LockType.WRITE)
    def removeSession(session: Session) {
        sessions = sessions - session
    }

    @Lock(LockType.READ)
    def sendToAll(messageType: String, message: Map[String, Any]) {
        def messageJson = gson.toJson(Map(
            "type" -> messageType,
            "data" -> message.asJava
        ).asJava)
        sessions.foreach((session) => {
            session.getBasicRemote.sendText(messageJson)
        })
    }

}
