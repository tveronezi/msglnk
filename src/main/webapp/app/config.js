window.ux = window.ux || {};
window.ux.YUI_CONF = {
    // Uncomment it to get the debug version of YUI
    //filter: 'debug',

    base: window.ux.ROOT_URL + 'webjars/yui/3.11.0/',

    groups: {
        'ux': {
            modules: {
                'ux-console': {
                    path: '../../../app/js/console.js'
                },
                'ux-class': {
                    path: '../../../app/js/class.js'
                },
                'ux-app': {
                    requires: ['app', 'json-parse', 'ux-lib-less', 'ux-lib-jquery', 'ux-lib-bootstrap', 'ux-console', 'ux-class',
                        'ux-view-about', 'ux-view-home', 'ux-view-email-send', 'ux-keep-alive', 'ux-growl', 'ux-sequence'
                    ],
                    path: '../../../app/js/app.js'
                },
                'ux-i18n': {
                    requires: ['ux-console', 'io-base', 'handlebars'],
                    path: '../../../app/js/i18n.js'
                },
                'ux-sequence': {
                    path: '../../../app/js/sequence.js'
                },
                'ux-keep-alive': {
                    requires: ['ux-console', 'io-base'],
                    path: '../../../app/js/keep-alive.js'
                },
                'ux-templates': {
                    requires: ['ux-console', 'io-base', 'handlebars', 'ux-i18n'],
                    path: '../../../app/js/templates.js'
                },

                // Views
                'ux-view-about': {
                    requires: ['base', 'ux-templates'],
                    path: '../../../app/js/view/about.js'
                },
                'ux-view-home': {
                    requires: ['base', 'ux-templates'],
                    path: '../../../app/js/view/home.js'
                },
                'ux-view-email-send': {
                    requires: ['base', 'ux-templates'],
                    path: '../../../app/js/view/email-send.js'
                },
                'ux-growl': {
                    requires: ['base', 'ux-templates', 'anim'],
                    path: '../../../app/js/view/growl.js'
                }
            }

        },
        'lib': {
            modules: {
                // External lib
                'ux-lib-less': {
                    path: '../../../app/lib/less/less.min.js'
                },
                'ux-lib-jquery': {
                    path: '../../../app/lib/jquery/jquery.min.js'
                },
                'ux-lib-bootstrap': {
                    requires: ['ux-lib-jquery', 'ux-lib-bootstrap-css'],
                    path: '../../../app/lib/bootstrap/js/bootstrap.min.js'
                },
                'ux-lib-bootstrap-css': {
                    type: 'css',
                    path: '../../../app/lib/bootstrap/css/bootstrap.css'
                }
            }
        }
    }
};