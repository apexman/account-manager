define([
        "webix",
        "form/account/editForm",
        "form/account/depositForm",
        "form/account/withdrawForm",
        "form/transaction/transferForm"
    ],
    function (webix, accountEditForm, depositForm, withdrawForm, transferForm) {

        function listGrid() {
            let accountList =
                {
                    view: "datatable",
                    id: "accountList",
                    url: databaseUrl + "/account/",
                    autowidth: true,
                    select: "row",
                    type: {height: "auto"},
                    columns: [
                        {id: "id", header: "ID", width: 50, hidden: true, sort: "string"},
                        {id: "name", header: "Name", width: 200, sort: "string"},
                        {id: "balance", header: "Balance", width: 200},
                        {
                            id: "",
                            template: "<input class='depositBtn' type='button' value='Deposit'>",
                            css: "padding_less",
                            width: 100
                        },
                        {
                            id: "",
                            template: "<input class='withdrawBtn' type='button' value='Withdraw'>",
                            css: "padding_less",
                            width: 100
                        },
                        {
                            id: "",
                            template: "<input class='delbtn' type='button' value='Delete'>",
                            css: "padding_less",
                            width: 100
                        },
                        {
                            id: "",
                            template: "<input class='transferBtn' type='button' value='Transfer'>",
                            css: "padding_less",
                            width: 100
                        },
                    ],
                    on: {
                        onItemDblClick: function (rowClicked) {
                            let account = $$("accountList").getItem(rowClicked);
                            accountEditForm.show(account);
                        },
                        onAfterLoad: function () {
                            this.sort("name", "asc");
                            this.markSorting("name", "asc");
                        }
                    }
                };

            return accountList;
        }

        return {
            grid: listGrid(),

            show: function () {
                webix.ui({
                    view: "window",
                    id: "accountListWindow",
                    height: 500,
                    position: "center",
                    move: true,
                    modal: true,
                    head: {
                        view: "toolbar", cols: [
                            {view: "label", label: "Accounts"},
                            {
                                view: "icon", icon: "times",
                                click: function () {
                                    $$("accountListWindow").close();
                                }
                            }
                        ]
                    },
                    body: listGrid()
                }).show();

                $$("accountList").on_click.depositBtn = function (e, id, trg) {
                    let depositAcc = $$("accountList").getItem(id);
                    depositForm.show(depositAcc);

                    //block default onclick event
                    return false;
                };

                $$("accountList").on_click.withdrawBtn = function (e, id, trg) {
                    let withdrAcc = $$("accountList").getItem(id);
                    withdrawForm.show(withdrAcc);

                    //block default onclick event
                    return false;
                };

                $$("accountList").on_click.delbtn = function (e, id, trg) {
                    let deletingAccount = $$("accountList").getItem(id);
                    accountEditForm.deleteAccount(deletingAccount);

                    //block default onclick event
                    return false;
                };
                $$("accountList").on_click.transferBtn = function (e, id, trg) {
                    let fromAcc = $$("accountList").getItem(id);
                    transferForm.show(fromAcc, null);

                    //block default onclick event
                    return false;
                };

            }
        }
    });
