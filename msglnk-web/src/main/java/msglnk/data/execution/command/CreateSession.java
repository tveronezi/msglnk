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

package msglnk.data.execution.command;

import msglnk.data.entity.MailSession;
import msglnk.data.entity.SessionParameter;
import msglnk.data.execution.BaseEAO;
import msglnk.data.execution.DbCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateSession implements DbCommand<MailSession> {

    public String name;
    public String account;
    public String password;
    public Map<String, String> parametersMap;

    private SessionParameter createParameter(MailSession session, String key, String value) {
        final SessionParameter result = new SessionParameter();
        result.setKey(key);
        result.setValue(value);
        result.setMailSession(session);
        return result;
    }

    @Override
    public MailSession execute(BaseEAO eao) {
        MailSession result = new MailSession();

        result.setName(this.name);
        result.setAccount(this.account);
        result.setPassword(this.password);

        result = eao.create(result);

        final List<SessionParameter> parameters = new ArrayList<SessionParameter>();
        if (this.parametersMap != null) {
            for (String key : this.parametersMap.keySet()) {
                parameters.add(createParameter(result, key, this.parametersMap.get(key)));
            }
        }
        result.setParameters(parameters);
        return result;
    }
}
