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

YUI.add('ux-view-home', function (Y) {
    'use strict';

    Y.ux.Class.createClass('ux.view.Home', Y.View, {
        events: {
            '.ux-read-btn': {click: 'triggerRead'},
            '.ux-save-btn': {click: 'saveSession'}
        },
        saveSession: function () {
            var txt = this.get('container').one('.ux-session-properties');
            var value = txt.get('value');
            this.fire('ux-save-email-session', {
                config: value
            });
            this.render();
        },
        triggerRead: function () {
            this.fire('ux-trigger-read-emails', {});
        },
        render: function () {
            this.get('container').setHTML(Y.ux.Templates.build('home'));
            return this;
        }
    });

});