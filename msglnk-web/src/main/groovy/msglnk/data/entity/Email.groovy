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
@Table(name = 'mail_tbl')
class Email extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "msg_session_id", nullable = false, updatable = false)
    MailSession session

    @ManyToOne(optional = false)
    @JoinColumn(name = "msg_from_id", nullable = false, updatable = false)
    EmailAddress from;

    @ManyToMany
    @JoinTable(name = "emails_recipients_tbl")
    List<EmailAddress> to = new ArrayList<>();

    @Column(name = "msg_subject", nullable = false, updatable = false)
    String subject;

    @Column(name = "msg_text", nullable = false, updatable = false)
    String text;

    @Column(name = 'msg_type', nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    EmailType emailType;

    @Column(name = 'msg_notified', nullable = false)
    Boolean notified = Boolean.FALSE;

    @PrePersist
    void normalize() {
        if (this.subject) {
            this.subject = this.subject.trim()
        }
        if (this.text) {
            this.text = this.text.trim()
        }
    }

    @Override
    public String toString() {
        return "Email{session=${session.name}, from=${from}, to=${to}, subject='${subject}', " +
                "text='${text}', emailType=${emailType}}"
    }
}
