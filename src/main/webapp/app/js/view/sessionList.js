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
            me.ux = me.ux || {};
            if (!me.ux.isRendered) {
                me.get('container').setHTML(Y.ux.Templates.build('session-list'));
                me.ux.isRendered = true;
            }
            return me;
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