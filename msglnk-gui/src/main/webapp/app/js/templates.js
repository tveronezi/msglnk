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

YUI.add('ux-templates', function (Y) {
    'use strict';

    // The names of the Handlebars templates without the ".handlebars" extension.
    var files = [
        'about',
        'home',
        'email-send'
    ];

    // A map will all the handlebars templates.
    var templates = {};

    // This is what users will see from outside this closure.
    var output = Y.namespace('ux.Templates');

    function getTpl(key) {
        var tpl = templates[key];
        if (!tpl) {
            throw 'Template not found. key: ' + key;
        }
        return tpl;
    }

    output.build = function (key, cfg) {
        var tpl = getTpl(key);
        if (cfg) {
            return tpl(cfg);
        }
        return tpl({});
    };

    // Load all the files synchronous.
    Y.Array.forEach(files, function (file) {
        Y.io(ROOT_URL + 'app/js/templates/' + file + '.handlebars', {
            sync: true,
            on: {
                success: function (a, b, c, d, e) {
                    templates[file] = Y.Handlebars.compile(b.responseText);
                },
                failure: function () {
                    window.console.error('Impossible to load template file', file);
                }
            }
        });
    });

    // Handlebars helper to load i18n values inside the templates directly.
    Y.Handlebars.registerHelper('i18n', function (key) {
        return Y.ux.Messages.get(key);
    });

});