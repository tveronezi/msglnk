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


YUI.add('ux-app', function (Y) {
    'use strict';

    var app = new Y.App({
        serverRouting: true,
        root: window.ux.ROOT_URL,
        views: {
            'home': {
                persist: false,
                type: 'ux.view.Home'
            },
            'email-send': {
                persist: false,
                type: 'ux.view.EmailSend'
            },
            about: {
                persist: false,
                type: 'ux.view.About'
            }
        }
    });

    app.route('/', function (req) {
        app.showView('home', {});
    });

    app.route('/about', function (req) {
        app.showView('about', {});
    });

    app.route('/email-send', function (req) {
        app.showView('email-send', {});
    });

    app.on('EmailSend:ux-send-email', function (evt) {
        var data = evt.data;
        Y.io(window.ux.ROOT_URL + 'rest/email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: Y.JSON.stringify({
                emailDto: data
            }),
            on: {
                failure: function (transactionid, response, args) {
                    Y.ux.Growl.showNotification('error', Y.ux.Messages.get('email.send.error'));
                    app.showView('email-send', {});
                },
                success: function (transactionid, response, args) {
                    Y.ux.Growl.showNotification('success', Y.ux.Messages.get('email.send.success'));
                    app.showView('email-send', {});
                }
            }
        });
    });

    app.on('EmailSend:ux-cancel-email', function (evt) {
        app.showView('email-send', {});
    });

    app.on('Home:ux-save-email-session', function (evt) {
        Y.io(window.ux.ROOT_URL + 'rest/session', {
            method: 'POST',
            data: evt,
            on: {
                failure: function () {
                    Y.ux.Growl.showNotification('error', Y.ux.Messages.get('save.session.error'));
                },
                success: function (transactionid, response, args) {
                    Y.ux.Growl.showNotification('success', Y.ux.Messages.get('save.session.success', {
                        name: evt.name
                    }));
                }
            }
        });
    });

    app.on('Home:ux-trigger-read-emails', function (evt) {
        Y.io(window.ux.ROOT_URL + 'rest/email/trigger-read', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            on: {
                start: function () {
                    Y.ux.Growl.showNotification('info', Y.ux.Messages.get('email.read.triggered'));
                },
                failure: function () {
                    Y.ux.Growl.showNotification('error', Y.ux.Messages.get('email.read.error'));
                },
                success: function () {
                    Y.ux.Growl.showNotification('success', Y.ux.Messages.get('email.read.success'));
                }
            }
        });
    });

    app.render().dispatch();
});
