/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package msglnk.data

import scala.reflect.BeanProperty
import javax.persistence._
import msglnk.exception.InvalidParameterException

@Entity
@Table(uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("session_name"))))
class MailSession extends BaseEntity {

    @Column(name = "session_name", nullable = false)
    @BeanProperty
    var name: String = _

    @Column(name = "session_username", nullable = true)
    @BeanProperty
    var userName: String = _

    @Column(name = "session_userpassword", nullable = true)
    @BeanProperty
    var userPassword: String = _

    @Column(name = "session_config", nullable = false)
    @Lob
    @BeanProperty
    var config: String = _

    @PrePersist
    def prePersist() {
        def getException(fieldName: String) = {
            new InvalidParameterException("The field %s cannot be empty or null".format(fieldName))
        }
        def trimAndValidate(fieldName: String, value: String): String = {
            if (value == null) {
                throw getException(fieldName)
            } else {
                value.trim() match {
                    case "" => throw getException(fieldName)
                    case s => s
                }
            }
        }
        userName = trimAndValidate("userName", userName)
        userPassword = trimAndValidate("userPassword", userPassword)
        name = trimAndValidate("name", name)
    }

}
