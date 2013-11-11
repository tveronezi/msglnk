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

    var sessionsList = new Y.ux.model.MailSessions({});
    var sessionsListView = new Y.ux.view.SessionList({
        modelList: sessionsList
    });

    var app = new Y.App({
        serverRouting: true,
        root: window.ux.ROOT_URL,
        views: {
            'home': {
                type: 'ux.view.Home'
            }
        }
    });
    app.showView('home', {});

    var showView = function (viewObj) {
        var mainContainer = app.views.home.instance.get('container').one('.ux-content');
        mainContainer.setHTML(viewObj.render().get('container'));
    };

    var showListView = function () {
        showView(sessionsListView);
        sessionsList.load();
    };

    var showEditView = function (id) {
        var showIt = function (model) {
            var sessionsEditView = new Y.ux.view.SessionEdit({
                model: model
            });
            sessionsEditView.on('ux-save-email-session', function (evt) {
                evt.model.save({}, function (err) {
                    if (err) {
                        Y.ux.Growl.showNotification('danger', Y.ux.Messages.get('save.session.error'));
                    } else {
                        Y.ux.Growl.showNotification('success', Y.ux.Messages.get('save.session.success', {
                            name: evt.name
                        }));
                        app.navigate('/', {
                            force: false
                        });
                        showListView();
                    }
                });
            });
            showView(sessionsEditView);
        };
        if (id !== null && id !== undefined) {
            (function () {
                var model = new Y.ux.model.MailSession({
                    id: id
                });
                model.load(function (err) {
                    if (err) {
                        app.navigate('/', {
                            force: false
                        });
                        showListView();
                    } else {
                        showIt(model);
                    }
                });
            }());
        } else {
            showIt(new Y.ux.model.MailSession({}));
        }
    };

    app.route('/', function () {
        showListView();
    });
    app.route('/session/add', function () {
        showEditView(null);
    });
    app.route('/session/view/:id', function (req) {
        showEditView(req.params.id);
    });

    sessionsListView.on('ux-trigger-session-add', function () {
        app.navigate('/session/add', {
            force: false
        });
        showEditView(null);
    });

    app.render().dispatch();
});
