require.config({
    paths: {
        webix: "libs/webix/webix",
        dateFormat: "libs/dateFormat.min"
    },
    shim: {
        // non-AMD.
        "webix": {
            exports: "webix"
        },
        "dateFormat": {
            exports: "dateFormat"
        }
    }
});

require(
    [
        "webix",
        "view/home"
    ],
    function (webix, home) {
        webix.ready(function () {
            home.show();
        });
    }
);