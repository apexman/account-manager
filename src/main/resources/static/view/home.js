define(
    [
        "webix",
        "dateFormat",
        "view/mainMenu"
    ],
    function (webix, dateFormat, mainMenu) {
        function prepareInterface() {
            mainMenu.show();
        }

        return {
            show: function () {
                prepareInterface();
            }
        }
    }
);