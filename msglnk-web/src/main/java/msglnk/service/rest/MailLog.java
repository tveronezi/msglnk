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

package msglnk.service.rest;

import msglnk.data.dto.EmailLogDto;
import msglnk.data.entity.MailSessionLog;
import msglnk.service.bean.DtoBuilderImpl;
import msglnk.service.bean.MailImpl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/email-log")
@Produces("application/json")
@Stateless
public class MailLog {

    @EJB
    private MailImpl mailImpl;

    @EJB
    private DtoBuilderImpl builder;

    @GET
    public List<EmailLogDto> get() {
        final List<MailSessionLog> logs = this.mailImpl.getLogs();
        return this.builder.buildLogs(logs);
    }
}
