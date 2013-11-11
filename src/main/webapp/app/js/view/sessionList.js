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

YUI.add('ux-view-session-list', function (Y) {
    'use strict';

    Y.ux.Class.createClass('ux.view.SessionList', Y.View, {
        events: {
            '.ux-add-btn': {click: 'triggerAdd'}
        },
        triggerAdd: function (evt) {
            evt.preventDefault();
            this.fire('ux-trigger-session-add', {});
        },
        render: function () {
            var me = this;
            var container = me.get('container');
            container.setHTML(Y.ux.Templates.build('session-list'));
            var list = container.one('.list-group');
            var modelList = me.get('modelList');
            modelList.each(function (model) {
                var content = Y.ux.Templates.build('session-list-entry', {
                    id: model.get('id'),
                    name: model.get('name')
                });
                var modelNode = Y.Node.create(content);
                list.append(modelNode);
            });
            return me;
        },
        initializer: function () {
            var me = this;
            var list = me.get('modelList');
            list.after('load', me.render, me);
            list.after('*:change', me.render, me);
        }
    }, {
        ATTRS: {
            // Override the default container attribute.
            container: {
                valueFn: function () {
                    return Y.Node.create('<div class="panel panel-default ux-session-list"/>');
                }
            }
        }
    });
});