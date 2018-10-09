define(["webix"],
    function (webix) {

        function editForm(account) {
            if (account == null)
                account = {};

            let form = {
                view: "form",
                id: "accountDepositForm",
                width: 300,
                elements: [
                    {
                        view: "text",
                        label: "ID",
                        name: "id",
                        value: account.id,
                        readonly: true,
                        disabled: true,
                        bottomPadding: 18,
                        hidden: true
                    },
                    {
                        view: "text",
                        label: "Name",
                        name: "name",
                        value: account.name,
                        readonly: true,
                        disabled: true,
                        bottomPadding: 18,
                    },
                    {
                        view: "text",
                        label: "Balance",
                        name: "balance",
                        value: account.balance,
                        attributes: {type: "number"},
                        bottomPadding: 18,
                        readonly: true,
                        disabled: true,
                    },
                    {
                        view: "text",
                        label: "Deposit",
                        name: "deposit",
                        required: true,
                        attributes: {type: "number"},
                        bottomPadding: 36,
                        invalidMessage: "Balance must be a positive number",
                        validate: function (val) {
                            return !isNaN(val * 1) && webix.rules.isNumber(val) && val > 0;
                        }
                    },
                    {
                        margin: 5, cols: [
                            {
                                view: "button", value: "Deposit", type: "form",
                                click: function () {
                                    if ($$("accountDepositForm").validate()) {
                                        // on success
                                        let account = $$("accountDepositForm").getValues();

                                        console.log("deposit: " + JSON.stringify(account));
                                        webix.message("Deposited on : " + account.name + " of " + account.deposit);
                                        webix.ajax()
                                            .post(databaseUrl + "/account/deposit/" + account.id, {deposit: account.deposit})
                                            .then(function (result) {
                                                let updatedAccount = result.json();
                                                let accountListUI = $$("accountList");
                                                if (accountListUI != null)
                                                    accountListUI.updateItem(updatedAccount.id, updatedAccount);

                                                $$("accountDepositWindow").close();
                                            })
                                            .fail(function (xhr) {
                                                let response = JSON.parse(xhr.response);
                                                webix.message({type: 'error',
                                                    text: response.errorMessage});
                                            });
                                    }
                                }
                            },
                            {
                                view: "button", value: "Cancel", click: function () {
                                    $$("accountDepositWindow").close();
                                }
                            }
                        ]
                    }
                ]
            };

            return form;
        }

        return {
            show: function (account) {
                webix.ui({
                    view: "window",
                    id: "accountDepositWindow",
                    height: 500,
                    position: "center",
                    move: true,
                    modal: true,
                    head: {
                        view: "toolbar", cols: [
                            {view: "label", label: ""},
                            {
                                view: "icon", icon: "times",
                                click: function () {
                                    $$("accountDepositWindow").close();
                                }
                            }
                        ]
                    },
                    body: editForm(account)
                }).show();

                $$("accountDepositForm").focus("deposit");
            }
        }
    });
