define([
        "webix",
        "entity/account/editForm"
    ],
    function (webix, accountEditForm) {

        function listGrid() {
            let accountList =
                {
                    view: "datatable",
                    id: "accountList",
                    url: databaseUrl + "/account/all",
                    autowidth: true,
                    select: "row",
                    type: {height: "auto"},
                    columns: [
                        {id: "id", header: "ID", width: 50, hidden: true, sort: "string"},
                        {id: "name", header: "Name", width: 200, sort: "string"},
                        {id: "balance", header: "Balance", width: 200},
                        {
                            id: "",
                            template: "<input class='delbtn' type='button' value='Delete'>",
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

                $$("accountList").on_click.delbtn=function(e, id, trg){
                    let deletingAccount = $$("accountList").getItem(id);
                    accountEditForm.deleteAccount(deletingAccount)

                    //block default onclick event
                    return false;
                };

            }
        }
    });
