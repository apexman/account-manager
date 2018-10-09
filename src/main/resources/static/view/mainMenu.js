define(
    [
        "webix",
        "form/account/browseAccounts",
        "form/account/editForm"
    ],
    function (webix, accountList, accountEditForm) {
        webix.ui({
            view: "window",
            id: "mainMenu",
            position: "center",
            // heigth: 400,
            // width: 400,
            modal: true,
            head: false,
            body: {
                rows:[
                    {
                        cols:[
                            {
                                view: "button",
                                id: "createAccount",
                                value: "Create account",
                                type: "form",
                                height: 300,
                                width: 300,
                                // inputHeigth: 150,
                                // inputWidth: 150,
                                click: function () {
                                    accountEditForm.show();
                                }
                            },
                            {
                                view: "button",
                                id: "browseToEdit",
                                value: "All accounts",
                                type: "form",
                                height: 300,
                                width: 300,
                                // inputHeight: 150,
                                // inputWidth: 150,
                                click: function () {
                                    accountList.show();
                                }
                            }
                        ]
                    },
                    // {
                    //     cols:[
                    //         {
                    //             view: "button",
                    //             id: "browseView",
                    //             value: "View document",
                    //             type: "form",
                    //             inputWidth: 150,
                    //             click: function () {
                    //                 documentList.show();
                    //             }
                    //         },
                    //         {
                    //             view: "button",
                    //             id: "more",
                    //             value: "MOOAAARR",
                    //             type: "form",
                    //             inputWidth: 150
                    //         }
                    //     ]
                    // }
                ]
            }
        });

        return{
            show: function () {
                $$("mainMenu").show();
            }
        }
    }
);