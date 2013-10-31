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

YUI.add('ux-sequence', function (Y) {
    'use strict';

    var values = {};

    function current(key) {
        var value = values[key];
        if (!value && value !== 0) {
            value = 0;
            values[key] = value;
        }
        return value;
    }

    function next(key) {
        var value = current(key);
        value += 1;
        values[key] = value;
        return value;
    }

    function get(key, closure) {
        return key + '-' + closure(key);
    }

    Y.namespace('ux.Sequence').current = current;

    Y.namespace('ux.Sequence').next = next;

    Y.Handlebars.registerHelper('current-id', function (key) {
        return get(key, current);
    });

    Y.Handlebars.registerHelper('next-id', function (key) {
        return get(key, next);
    });

});