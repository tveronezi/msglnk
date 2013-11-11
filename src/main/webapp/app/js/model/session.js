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

YUI.add('ux-model-session', function (Y) {
    'use strict';

    /*jslint stupid: true */ // Needed otherwise lint thinks "ModelSync" is a node.js "sync" method.

    var parse = function (response) {
        var json = Y.JSON.parse(response);
        return json.emailSessionDto;
    };

    var toJSON = function () {
        var attrs = this.getAttrs();
        delete attrs.clientId;
        delete attrs.destroyed;
        delete attrs.initialized;
        if (this.idAttribute !== 'id') {
            delete attrs.id;
        }
        return {
            emailSessionDto: attrs
        };
    };

    Y.ux.Class.createClass('ux.model.MailSession', Y.Model, {
        root: window.ux.ROOT_URL + 'rest/session',
        parse: parse,
        toJSON: toJSON
    }, {}, [Y.ModelSync.REST]);

    Y.ux.Class.createClass('ux.model.MailSessions', Y.ModelList, {
        model: Y.ux.model.MailSession,
        parse: parse
    }, {}, [Y.ModelSync.REST]);

});
