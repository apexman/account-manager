define(["webix"],
    function (webix) {

        function saveAccount(account) {
            if (account.id === "")
                delete account.id;

            console.log("saveAccount: " + JSON.stringify(account));

            webix.ajax()
                .post(databaseUrl + "/account/", account)
                .then(function (result) {
                    let updatedAccount = result.json();
                    let accountListUI = $$("accountList");
                    if (accountListUI != null) {
                        accountListUI.updateItem(updatedAccount.id, updatedAccount);
                    }
                    webix.message("Saved");
                })
                .fail(function (xhr) {
                    let response = JSON.parse(xhr.response);
                    webix.message({type: 'error',
                        text: response.errorMessage});
                });
        }

        function deleteAccount(account) {
            webix.confirm("Are you sure want to delete this account?", "confirm-warning", function(result){
                if (result === true) {
                    webix.ajax()
                        .del(databaseUrl + "/account/" + account.id)
                        .then(function () {
                            let accountListUI = $$("accountList");
                            if (accountListUI != null)
                                accountListUI.remove(account.id);
                            webix.message("Deleted row: " + account.name);
                        })
                        .fail(function (xhr) {
                            let response = JSON.parse(xhr.response);
                            webix.message({type: 'error',
                                text: response.errorMessage});
                        });
                }
            });
        }

        function editForm(account) {
            if (account == null)
                account = {};

            let form = {
                view: "form",
                id: "accountEditForm",
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
                        required: true,
                        invalidMessage: "Name can not be empty",
                        bottomPadding: 18,
                        validate: function (value) {
                            return value != null && value !== "";
                        }
                    },
                    {
                        view: "text",
                        label: "Balance",
                        name: "balance",
                        value: account.balance,
                        required: true,
                        attributes: {type: "number"},
                        invalidMessage: "Balance must be a non-negative number",
                        bottomPadding: 36,
                        validate: function (val) {
                            return !isNaN(val * 1) && webix.rules.isNumber(val) && val >= 0;
                        }
                    },
                    {
                        margin: 5, cols: [
                            {
                                view: "button", value: "Save", type: "form",
                                click: function () {
                                    if ($$("accountEditForm").validate()) {
                                        // on success
                                        let savingAccount = $$("accountEditForm").getValues();
                                        saveAccount(savingAccount);
                                        $$("accountEditFormWindow").close();
                                    }
                                }
                            },
                            {
                                view: "button", value: "Cancel", click: function () {
                                    $$("accountEditFormWindow").close();
                                }
                            }
                        ]
                    }
                ]
            };

            return form;
        }

        return {
            // layout: function (account) {
            //     return editForm(account)
            // },

            show: function (account) {
                webix.ui({
                    view: "window",
                    id: "accountEditFormWindow",
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
                                    $$("accountEditFormWindow").close();
                                }
                            }
                        ]
                    },
                    body: editForm(account)
                }).show();

                $$("accountEditForm").focus("name");
            },

            saveAccount: function (account) {
                saveAccount(account);
            },

            deleteAccount: function (account) {
                deleteAccount(account);
            }
        }
    });
