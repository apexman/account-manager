define(["webix"],
    function (webix) {

        function editForm(account) {
            if (account == null)
                account = {};

            let form = {
                view: "form",
                id: "accountWithdrawForm",
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
                        // required: true,
                        readonly: true,
                        disabled: true,
                        bottomPadding: 18,
                        // invalidMessage: "Name can not be empty",
                        // validate: function (value) {
                        //     return value != null && value !== "";
                        // }
                    },
                    {
                        view: "text",
                        label: "Balance",
                        name: "balance",
                        value: account.balance,
                        // required: true,
                        attributes: {type: "number"},
                        bottomPadding: 18,
                        readonly: true,
                        disabled: true,
                        // invalidMessage: "Balance must be a non-negative number",
                        // validate: function (val) {
                        //     return !isNaN(val * 1) && webix.rules.isNumber(val) && val >= 0;
                        // }
                    },
                    {
                        view: "text",
                        label: "Withdraw",
                        name: "withdraw",
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
                                view: "button", value: "Withdraw", type: "form",
                                click: function () {
                                    if ($$("accountWithdrawForm").validate()) {
                                        // on success
                                        let account = $$("accountWithdrawForm").getValues();

                                        console.log("withdraw: " + JSON.stringify(account));
                                        webix.message("Withdrawn on : " + account.name + " of " + account.withdraw);
                                        webix.ajax()
                                            .post(databaseUrl + "/account/withdraw/" + account.id, {withdrawn: account.withdraw})
                                            .then(function (result) {
                                                let updatedAccount = result.json();
                                                let accountListUI = $$("accountList");
                                                if (accountListUI != null)
                                                    accountListUI.updateItem(updatedAccount.id, updatedAccount);

                                                $$("accountWithdrawWindow").close();
                                            })
                                            .fail(function (xhr) {
                                                let response = JSON.parse(xhr.response);
                                                let errorCode = response.errorCode;
                                                let message = response.errorMessage;

                                                if (errorCode === 2)
                                                    webix.message({type: 'error',
                                                        text: "There is not enough money to withdraw from account " + account.name});
                                                else
                                                    webix.message({type: 'error',
                                                        text: message});
                                            });
                                    }
                                }
                            },
                            {
                                view: "button", value: "Cancel", click: function () {
                                    $$("accountWithdrawWindow").close();
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
                    id: "accountWithdrawWindow",
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
                                    $$("accountWithdrawWindow").close();
                                }
                            }
                        ]
                    },
                    body: editForm(account)
                }).show();

                $$("accountWithdrawForm").focus("withdraw");
            }
        }
    });
