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

import msglnk.cdi.DtoBuilder;
import msglnk.data.dto.MailSessionDto;
import msglnk.data.entity.MailSession;
import msglnk.service.bean.MailImpl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Path("/session")
@Produces("application/json")
@Stateless
public class Session {

    @EJB
    private MailImpl mailImpl;

    @Inject
    private DtoBuilder builder;

    @POST
    public void save(@FormParam("config") String config) throws IOException {
        final Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(config.getBytes()));
        this.mailImpl.persistSession(properties);
    }

    @GET
    public List<MailSessionDto> list() {
        final List<MailSession> sessions = this.mailImpl.getSessions();
        return this.builder.buildSessions(sessions);
    }
}
