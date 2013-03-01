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

YUI.add('ux-i18n', function (Y) {
    'use strict';

    var messages = {
        'app.name': 'MessageLink',
        'home': 'Home',
        'page': 'Page',
        'send.email': 'Send email',
        'read.email': 'Read email',
        'about': 'About',
        'send': 'Send',
        'cancel': 'Cancel',
        'session': 'Session',
        'name.of.email.session': 'Name of the email session',
        'send.an.email': 'Send an email',
        'to': 'To',
        'who.is.going.to.get.this.email': 'Who is going to get this email?',
        'subject': 'Subject',
        'heads.up': 'Heads up?',
        'content': 'Content',
        'trigger.email.read': 'Read emails from remote server',
        'emails': 'Emails',
        'search': 'Search',
        'dummy': 'dummy'
    };

    Y.namespace('ux.Messages').get = function (key, config) {
        var tpl = messages[key];
        if (!tpl) {
            window.console.error('i18n key not found', key);
            return '!!' + key + '!!';
        }
        if (config) {
            return tpl(config);
        }
        return tpl({});
    };

    Y.Object.each(messages, function (value, key) {
        messages[key] = Y.Handlebars.compile(value);
    });

});