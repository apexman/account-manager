define(["webix"],
    function (webix) {

        function editForm(fromAcc, whereAcc) {
            if (fromAcc == null)
                fromAcc = {};

            if (whereAcc == null)
                whereAcc = {};

            let fromAccFormElement = [
                {
                    view: "text",
                    label: "ID",
                    name: "fromAccId",
                    value: fromAcc.id,
                    readonly: true,
                    disabled: true,
                    bottomPadding: 18,
                    hidden: true
                },
                {
                    view: "text",
                    label: "Name",
                    name: "fromAccName",
                    value: fromAcc.name,
                    readonly: true,
                    disabled: true,
                    bottomPadding: 18,
                },
                {
                    view: "text",
                    label: "Balance",
                    name: "fromAccBalance",
                    value: fromAcc.balance,
                    attributes: {type: "number"},
                    bottomPadding: 18,
                    readonly: true,
                    disabled: true,
                }];

            let whereAccFormElement = [
                {
                    view: "text",
                    label: "ID",
                    name: "whereAccId",
                    value: whereAcc.id,
                    readonly: true,
                    disabled: true,
                    bottomPadding: 18,
                    hidden: true
                },
                {
                    view: "text",
                    label: "Name",
                    name: "whereAccName",
                    value: whereAcc.name,
                    readonly: true,
                    disabled: true,
                    bottomPadding: 18,
                },
                {
                    view: "text",
                    label: "Balance",
                    name: "whereAccBalance",
                    value: whereAcc.balance,
                    attributes: {type: "number"},
                    bottomPadding: 18,
                    readonly: true,
                    disabled: true,
                }];

            let form = {
                view: "form",
                id: "transferForm",
                width: 600,
                elements: [
                    {
                        rows: [
                            {
                                cols: [
                                    {rows: fromAccFormElement},
                                    {rows: whereAccFormElement},
                                    {
                                        // margin: 5, cols: [
                                        //     {
                                        //         view: "button", value: "Deposit", type: "form",
                                        //         click: function () {
                                        //             if ($$("accountDepositForm").validate()) {
                                        //                 // on success
                                        //                 let account = $$("accountDepositForm").getValues();
                                        //
                                        //                 console.log("deposit: " + JSON.stringify(account));
                                        //                 webix.message("Deposited on : " + account.name + " of " + account.deposit);
                                        //                 webix.ajax()
                                        //                     .post(databaseUrl + "/account/deposit/" + account.id, {deposit: account.deposit})
                                        //                     .then(function (result) {
                                        //                         let updatedAccount = result.json();
                                        //                         let accountListUI = $$("accountList");
                                        //                         if (accountListUI != null)
                                        //                             accountListUI.updateItem(updatedAccount.id, updatedAccount);
                                        //
                                        //                         $$("accountDepositWindow").close();
                                        //                     })
                                        //                     .fail(function (xhr) {
                                        //                         let response = JSON.parse(xhr.response);
                                        //                         webix.message({type: 'error',
                                        //                             text: response.errorMessage});
                                        //                     });
                                        //             }
                                        //         }
                                        //     },
                                        //     {
                                        //         view: "button", value: "Cancel", click: function () {
                                        //             $$("accountDepositWindow").close();
                                        //         }
                                        //     }
                                        // ]
                                    }

                                ]
                            },
                            {
                                rows: [
                                    {
                                        view: "text",
                                        label: "qwer"
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };

            return form;
        }

        return {
            show: function (fromAcc, whereAcc) {
                webix.ui({
                    view: "window",
                    id: "transactionWindow",
                    height: 500,
                    width: 500,
                    position: "center",
                    move: true,
                    modal: true,
                    head: {
                        view: "toolbar", cols: [
                            {view: "label", label: ""},
                            {
                                view: "icon", icon: "times",
                                click: function () {
                                    $$("transactionWindow").close();
                                }
                            }
                        ]
                    },
                    body: editForm(fromAcc, whereAcc)
                }).show();

                // $$("accountDepositForm").focus("deposit");
            }
        }
    });
