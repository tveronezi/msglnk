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

YUI.add('ux-growl', function (Y) {
    'use strict';

    var GrowlContainer = Y.Base.create('growlContainer', Y.View, [], {
        containerTemplate: Y.ux.Templates.build('growl-container'),
        render: function () {
            var container = this.get('container');
            if (container.inDoc()) {
                return; // already rendered
            }
            Y.one('body').append(container);
            return this;
        }
    });
    var growlContainer = new GrowlContainer({});
    growlContainer.render();

    // Taken from Underscorejs
    function debounce(func, wait, immediate) {
        var timeout, result;
        return function () {
            var context = this, args = arguments;
            var later = function () {
                timeout = null;
                if (!immediate) {
                    result = func.apply(context, args);
                }
            };
            var callNow = immediate && !timeout;
            window.clearTimeout(timeout);
            timeout = window.setTimeout(later, wait);
            if (callNow) {
                result = func.apply(context, args);
            }
            return result;
        };
    }

    function showNotification(messageType, messageText) {
        var alert = Y.Node.create(Y.ux.Templates.build('growl', {
            messageType: messageType
        }));
        alert.appendChild(Y.Node.create('<div class="message">' + messageText + '</div>'));
        growlContainer.get('container').appendChild(alert);
        new Y.Anim({
            node: alert,
            to: { opacity: 100 }
        }).run();

        var fadeOutAnim = new Y.Anim({
            node: alert,
            to: { opacity: 0 }
        });
        fadeOutAnim.on('end', function () {
            alert.remove(true);
        });

        var faceOutCallback = function () {
            fadeOutAnim.run();
        };
        var faceOut = debounce(faceOutCallback, 5000);
        faceOut();
    }

    Y.namespace('ux.Growl').showNotification = showNotification;
});
