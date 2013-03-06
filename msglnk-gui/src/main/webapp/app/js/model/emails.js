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

YUI.add('ux-model-email', function (Y) {
    'use strict';

    function execute(list, options, callback) {
        var data = $.extend(true, [], self._data);

        // SEARCHING
        if (options.search) {
            data = _.filter(data, function (item) {
                for (var prop in item) {
                    if (!item.hasOwnProperty(prop)) continue;
                    if (~item[prop].toString().toLowerCase().indexOf(options.search.toLowerCase())) return true;
                }
                return false;
            });
        }

        // FILTERING
        if (options.filter) {
            data = _.filter(data, function (item) {
                switch (options.filter.value) {
                    case 'lt5m':
                        if (item.population < 5000000) return true;
                        break;
                    case 'gte5m':
                        if (item.population >= 5000000) return true;
                        break;
                    default:
                        return true;
                        break;
                }
            });
        }

        var count = data.length;

        // SORTING
        if (options.sortProperty) {
            data = _.sortBy(data, options.sortProperty);
            if (options.sortDirection === 'desc') {
                data.reverse();
            }
        }

        // PAGING
        var startIndex = options.pageIndex * options.pageSize;
        var endIndex = startIndex + options.pageSize;
        var end = (endIndex > count) ? count : endIndex;
        var pages = Math.ceil(count / options.pageSize);
        var page = options.pageIndex + 1;
        var start = startIndex + 1;

        data = data.slice(startIndex, endIndex);

        callback({ data: data, start: start, end: end, count: count, pages: pages, page: page });
    }


    Y.ux.Class.createClass('ux.model.Email', Y.Model, {

    });

    var emailsModelList = new Y.ModelList({model: Y.ux.model.Email});
    Y.ux.emailsModelList = emailsModelList;

    Y.ux.Class.createClass('ux.model.EmailsDataSource', Y.Base, {
        columns: function () {
            return [
                {
                    property: 'from',
                    label: 'from',
                    sortable: true
                },
                {
                    property: 'to',
                    label: 'to',
                    sortable: true
                },
                {
                    property: 'subject',
                    label: 'subject',
                    sortable: true
                }
            ];

        },
        data: function (options, callback) {
            //options = {search, sortProperty, sortDirection, pageIndex, pageSize}
            execute(list, options, callback);
        }
    });

});
