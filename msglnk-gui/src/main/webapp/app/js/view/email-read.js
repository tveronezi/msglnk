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

YUI.add('ux-view-email-read', function (Y) {
    'use strict';

    Y.ux.Class.createClass('ux.view.EmailRead', Y.View, {
        events: {
            '.ux-read-btn': {click: 'triggerRead'},
            '.ux-refresh-btn': {click: 'triggerRefresh'}
        },
        triggerRead: function () {
            this.fire('ux-trigger-read-emails', {});
        },
        triggerRefresh: function () {
            this.fire('ux-trigger-refresh-emails', {});
        },
        render: function () {
            var grid = new Y.ux.view.Grid({
                title: Y.ux.Messages.get('emails'),
                columns: [
                    {
                        property: 'toponymName',
                        label: 'Name',
                        sortable: true
                    },
                    {
                        property: 'countrycode',
                        label: 'Country',
                        sortable: true
                    },
                    {
                        property: 'population',
                        label: 'Population',
                        sortable: true
                    },
                    {
                        property: 'fcodeName',
                        label: 'Type',
                        sortable: true
                    }
                ],
                data: data
            });
            grid.render();

            var container = this.get('container');
            container.setHTML(Y.ux.Templates.build('email-read'));
            container.one('.ux-grid-container').append(grid.get('container'));

            return this;
        }
    });

});