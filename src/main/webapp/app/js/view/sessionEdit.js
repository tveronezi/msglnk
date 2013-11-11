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

YUI.add('ux-view-session-edit', function (Y) {
    'use strict';

    Y.ux.Class.createClass('ux.view.SessionEdit', Y.View, {
        events: {
            '.ux-save-btn': {click: 'saveSession'},
            '.ux-delete-btn': {click: 'deleteSession'},
            '.ux-send-btn': {click: 'sendMail'}
        },
        sendMail: function(evt) {
            evt.preventDefault();
            var me = this;
            var model = me.get('model');
            me.fire('ux-send-email', {
                id: model.get('id')
            });
        },
        deleteSession: function (evt) {
            evt.preventDefault();
            var me = this;
            var model = me.get('model');
            me.fire('ux-delete-email-session', {
                model: model,
                name: model.get('name')
            });
        },
        saveSession: function (evt) {
            evt.preventDefault();
            var me = this;
            var model = me.get('model');
            var container = me.get('container');
            var setValue = function (fieldCss, fieldName) {
                var value = Y.Lang.trim(container.one(fieldCss).get('value'));
                if ('' !== value || Y.Lang.isValue(value)) {
                    model.set(fieldName, value);
                }
            };
            setValue('.ux-session-name', 'name');
            setValue('.ux-user-name', 'userName');
            setValue('.ux-user-password', 'userPassword');
            setValue('.ux-session-properties', 'config');
            me.fire('ux-save-email-session', {
                model: model,
                name: model.get('name')
            });
        },
        render: function () {
            var me = this;
            var model = me.get('model');
            var container = me.get('container');
            me.get('container').setHTML(Y.ux.Templates.build('session-edit'));
            var setValue = function (fieldCss, fieldName) {
                var value = model.get(fieldName);
                if (value !== null && value !== undefined) {
                    container.one(fieldCss).set('value', value);
                }
            };
            setValue('.ux-session-properties', 'config');
            setValue('.ux-session-name', 'name');
            setValue('.ux-user-name', 'userName');
            setValue('.ux-user-password', 'userPassword');
            if (!Y.Lang.isValue(model.get('id'))) {
                container.one('.ux-delete-btn').remove(true);
            }
            return me;
        }
    }, {
        ATTRS: {
            // Override the default container attribute.
            container: {
                valueFn: function () {
                    return Y.Node.create('<div class="panel panel-default ux-session-edit"/>');
                }
            }
        }
    });
});