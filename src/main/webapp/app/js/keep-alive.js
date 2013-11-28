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

YUI.add('ux-keep-alive', function (Y) {
    'use strict';

    var DELAY = 1000 * 60 * 4; // 4 minutes
    var timeoutKey = null;

    function scheduleNext() {
        if (timeoutKey !== null) {
            window.clearInterval(timeoutKey);
            window.console.log('keep-alive callback canceled.', timeoutKey);
            timeoutKey = null;
        }
        function timeoutCallback() {
            Y.io(window.ux.ROOT_URL + 'rest/keep-alive', {
                method: 'GET',
                on: {
                    success: function () {
                        scheduleNext();
                    },
                    failure: function () {
                        window.console.error('keep-alive callback error.');
                        window.setTimeout(function () {
                            window.location.reload();
                        }, 10000);
                    }
                }
            });
        }

        timeoutKey = window.setTimeout(timeoutCallback, DELAY);
        window.console.log('keep-alive callback created.', timeoutKey);
    }

    function connectSocket() {
        var location = window.location;
        var protocol = 'ws';
        if (location.protocol === 'https:' || location.protocol === 'https') {
            protocol = 'wss';
        }
        var wsPath = protocol + '://' + location.hostname + ':' + location.port + window.ux.ROOT_URL + 'ws/connection';
        var connection = new window.WebSocket(wsPath);
        connection.onopen = function () {
            window.console.log('WebSocket: connection started.');
        };
        connection.onerror = function () {
            // reload application
            window.location.reload();
        };
        connection.onmessage = function (e) {
            try {
                var evtData = Y.JSON.parse(e.data);
                Y.fire(evtData.type, evtData.data);
            } catch (ex) {
                window.console.error('WebSocket: parse -> ' + ex);
            }
            window.console.log('WebSocket: message -> ' + e.data);
        };
    }

    Y.on('io:start', function () {
        scheduleNext();
    });
    scheduleNext();
    connectSocket();
});
