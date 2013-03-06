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

package msglnk.service.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Schedule;

@RunAs("solution-admin")
public class EmailReaderRobot {
    private static final Logger LOG = LoggerFactory.getLogger(EmailReaderRobot.class);

    @EJB
    private MailImpl mail;

    @Schedule(minute = "*/15", hour = "*")
    public void readEmails() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Email reader triggered.");
        }
        // read the emails every 15 minutes
        this.mail.readEmails();
    }
}