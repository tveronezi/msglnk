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

if (window.document.location.href + '/' === window.document.location.origin + ROOT_URL) {
    window.location = window.document.location.href + '/';
}

YUI.add('ux-app', function (Y) {
    'use strict';

    var app = new Y.App({
        serverRouting: true,
        root: ROOT_URL,
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
        Y.io(ROOT_URL + 'rest/email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: Y.JSON.stringify({
                emailDto: data
            }),
            on: {
                complete: function (transactionid, response, args) {
                    app.showView('email-send', {});
                }
            }
        });
    });

    app.on('EmailSend:ux-cancel-email', function (evt) {
        app.showView('email-send', {});
    });

    app.on('Home:ux-save-email-session', function (evt) {
        Y.io(ROOT_URL + 'rest/session', {
            method: 'POST',
            data: {
                config: evt.config
            },
            on: {
                complete: function (transactionid, response, args) {
                    // no-op
                }
            }
        });
    });

    app.on('Home:ux-trigger-read-emails', function (evt) {
        Y.io(ROOT_URL + 'rest/email/trigger-read', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            on: {
                complete: function (transactionid, response, args) {
                    // no-op
                }
            }
        });
    });

    app.render().dispatch();
});
