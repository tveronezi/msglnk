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

package msglnk.data.entity

import javax.persistence.*

@Entity
@Table(name = 'mail_session_tbl', uniqueConstraints = @UniqueConstraint(columnNames = ['mail_session_name']))
class MailSession extends BaseEntity {

    @Column(name = 'mail_session_name', nullable = false)
    String name

    @Column(name = 'mail_user', nullable = false)
    String account

    @Column(name = 'mail_password', nullable = false)
    String password

    @OneToMany(cascade = CascadeType.ALL, mappedBy = 'mailSession')
    List<SessionParameter> parameters

}
