define(["webix"],
    function (webix) {

        function editForm(fromAcc) {
            if (fromAcc == null)
                fromAcc = {};

            let fromAccFormElement = [
                {
                    id: "fromAccId",
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
                    id: "fromAccName",
                    view: "text",
                    label: "From",
                    name: "fromAccName",
                    value: fromAcc.name + "(" + fromAcc.balance + ")",
                    readonly: true,
                    disabled: true,
                    bottomPadding: 18,
                },
                {
                    view: "text",
                    label: "From account balance",
                    name: "fromAccBalance",
                    value: fromAcc.balance,
                    attributes: {type: "number"},
                    bottomPadding: 18,
                    readonly: true,
                    disabled: true,
                    hidden: true
                }];

            function parseAccountsToCombo(listBody, name) {
                let nameParameter;
                if (name != null && name != '') {
                    nameParameter = "?name=" + name;
                } else {
                    nameParameter = "/";
                }
                return webix.ajax().bind(this).get(databaseUrl + "/account" + nameParameter, function (data) {
                    data = JSON.parse(data);
                    data = data.map(function (account) {
                        return {
                            id: account.id,
                            value: account.name,
                            // balance: account.balance
                        };
                    });
                    listBody.parse(data);
                });
            }

            let store = new webix.DataCollection({
                url: databaseUrl + "/account/",
                map: {
                    id: "#id#",
                    value: "#name# (#balance#)"
                }
            });


            let whereAccForm = {
                id: "whereAccForm",
                view: "combo",
                label: 'Where',
                options: {
                    body: {
                        dataFeed: function (str) {
                            return parseAccountsToCombo(this, str);
                        },
                        data: store.data,
                        ready: function () {
                            $$("whereAccForm").setValue(this.data.getFirstId())
                        },
                    }
                }
            };

            // $$("whereAccForm").getList().data.sync(store.data);

            let buttonsForm = {
                margin: 5,
                cols: [
                    {
                        view: "button",
                        value: "Transfer",
                        type: "form",
                        click: function () {
                            if ($$("transferForm").validate()) {
                                // on success
                                let fromAccId = $$("fromAccId").getValue();

                                let whereAccId = $$("whereAccForm").getValue();
                                let whereAccName = $$("whereAccForm").getText();

                                let money = $$("transferMoney").getValue();

                                console.log("Transaction: " + JSON.stringify(whereAccName));
                                webix.message("Transfer to : " + whereAccName + " ...");
                                webix.ajax()
                                    .post(databaseUrl + "/account/transfer", {
                                        idFrom: fromAccId,
                                        idWhere: whereAccId,
                                        money: money
                                    })
                                    .then(function () {
                                        let fromAcc = $$("accountList").getItem(fromAccId);
                                        let whereAcc = $$("accountList").getItem(whereAccId);

                                        fromAcc.balance = Number(fromAcc.balance) - Number(money);
                                        whereAcc.balance = Number(whereAcc.balance) + Number(money);

                                        $$("accountList").refresh();

                                        $$("transactionWindow").close();
                                    })
                                    .fail(function (xhr) {
                                        let response = JSON.parse(xhr.response);
                                        webix.message({
                                            type: 'error',
                                            text: response.errorMessage
                                        });
                                    });
                            }
                        }
                    },
                    {
                        view: "button", value: "Cancel", click: function () {
                            $$("transactionWindow").close();
                        }
                    }
                ]
            };

            let balanceForm = {
                id: "transferMoney",
                view: "text",
                attributes: {type: "number"},
                invalidMessage: "Balance must be a positive number",
                validate: function (val) {
                    return !isNaN(val * 1) && webix.rules.isNumber(val) && val > 0;
                }
            };

            let form = {
                view: "form",
                id: "transferForm",
                width: 600,
                elements: [
                    {rows: fromAccFormElement},
                    whereAccForm,
                    balanceForm,
                    buttonsForm
                ]
            };

            return form;
        }

        return {
            show: function (fromAcc) {
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
                    body: editForm(fromAcc)
                }).show();
            }
        }
    });
