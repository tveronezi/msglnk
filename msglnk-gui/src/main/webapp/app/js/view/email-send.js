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

YUI.add('ux-view-email-send', function (Y) {
    'use strict';

    Y.ux.Class.createClass('ux.view.EmailSend', Y.View, {
        events: {
            '.ux-send-btn': {click: 'submitForm'},
            '.ux-cancel-btn': {click: 'cancelSubmit'}
        },
        submitForm: function () {
            var values = {};
            var container = this.get('container');

            container.all('button').each(function (btn) {
                btn.setAttribute('disabled', true);
            });

            function getValues() {
                Y.each(arguments, function (selector) {
                    container.all(selector).each(function (txt) {
                        txt.setAttribute('disabled', true);
                        values[txt.get('name')] = txt.get('value');
                    });
                });
            }

            getValues('input', 'textarea');
            this.fire('ux-send-email', {
                data: values
            });
        },
        cancelSubmit: function () {
            this.fire('ux-cancel-email', {});
        },
        render: function () {
            this.get('container').setHTML(Y.ux.Templates.build('email-send'));
            return this;
        }
    });

});