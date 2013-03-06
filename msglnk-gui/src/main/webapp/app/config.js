var YUI_CONF = {
    // Uncomment it to get the debug version of YUI
    //filter: 'debug',

    base: ROOT_URL,

    groups: {
        'ux': {
            modules: {
                'ux-console': 'app/js/console.js',
                'ux-class': 'app/js/class.js',
                'ux-app': {
                    requires: ['app', 'json-parse', 'ux-lib-less', 'ux-lib-bootstrap', 'ux-console', 'ux-class',
                        'ux-view-about', 'ux-view-home', 'ux-view-email-send'
                    ],
                    path: 'app/js/app.js'
                },
                'ux-i18n': {
                    requires: ['ux-console', 'io-base', 'handlebars'],
                    path: 'app/js/i18n.js'
                },
                'ux-templates': {
                    requires: ['ux-console', 'io-base', 'handlebars', 'ux-i18n'],
                    path: 'app/js/templates.js'
                },

                // Views
                'ux-view-about': {
                    requires: ['base', 'ux-templates'],
                    path: 'app/js/view/about.js'
                },
                'ux-view-home': {
                    requires: ['base', 'ux-templates'],
                    path: 'app/js/view/home.js'
                },
                'ux-view-email-send': {
                    requires: ['base', 'ux-templates'],
                    path: 'app/js/view/email-send.js'
                }
            }

        },
        'lib': {
            modules: {
                // External lib
                'ux-lib-less': {
                    path: 'app/lib/less/less-1.3.0.min.js'
                },
                'ux-lib-jquery': {
                    fullpath: 'http://code.jquery.com/jquery-1.9.1.min.js'
                },
                'ux-lib-bootstrap': {
                    requires: ['ux-lib-jquery', 'ux-lib-bootstrap-css-responsive'],
                    path: 'app/lib/bootstrap/2.1.1/js/bootstrap.js'
                },
                'ux-lib-bootstrap-css-responsive': {
                    requires: ['ux-lib-bootstrap-css'],
                    type: 'css',
                    path: 'app/lib/bootstrap/2.1.1/css/bootstrap-responsive.css'
                },
                'ux-lib-bootstrap-css': {
                    type: 'css',
                    path: 'app/lib/bootstrap/2.1.1/css/bootstrap.css'
                }
            }
        }
    }
};