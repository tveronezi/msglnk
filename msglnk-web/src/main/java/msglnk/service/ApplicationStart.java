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

package msglnk.service;

import msglnk.SystemException;
import msglnk.service.bean.MailImpl;

import javax.annotation.PostConstruct;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton(name = "MsglnkApplicationStart")
@Startup
@RunAs("solution-admin")
public class ApplicationStart {

    @EJB
    private MailImpl mail;

    private void loadPropertiesFile(Properties config, File configFile) {
        try {
            config.load(new FileInputStream(configFile));
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }

    @PostConstruct
    public void applicationStartup() {
        final String configDirPath = System.getProperty("mailSessionsConfigDirPath", "");
        if ("".equals(configDirPath.trim())) {
            // no-op
            return;
        }

        final File configDir = new File(configDirPath);
        if (!configDir.exists() || !configDir.isDirectory()) {
            // no-op
            return;
        }

        final File[] configFiles = configDir.listFiles();
        if (configFiles.length == 0) {
            // no-op
            return;
        }

        for (File configFile : configFiles) {
            final Properties config = new Properties();
            loadPropertiesFile(config, configFile);

            final String name = config.getProperty("ux_session_name", configFile.getName());
            String account = null;
            String password = null;
            final Map<String, String> configMap = new HashMap<String, String>();
            for (String key : config.stringPropertyNames()) {
                if ("ux_session_user_account".equals(key)) {
                    account = config.getProperty(key);
                } else if ("ux_session_user_password".equals(key)) {
                    password = config.getProperty(key);
                } else {
                    configMap.put(key, config.getProperty(key));
                }
            }

            try {
                this.mail.persistSession(name, account, password, configMap);
            } catch (Exception e) {
                throw new SystemException("Unable to persist session. Check the config file. File: "
                        + configFile.getAbsolutePath());
            }
        }
    }
}
