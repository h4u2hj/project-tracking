sap.ui.define([
    "sap/ui/test/opaQunit"
], function (opaTest) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Basic operations test");

            opaTest("#0 Start application", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/CompletedProjectService" });
                Given.iResetTestData();
                Given.iStartMyApp();
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                When.onTheProjectsList.onFilterBar().iExecuteSearch();
                Then.onTheProjectsList.onTable().iCheckRows();
            });

            opaTest("#2: ListReport: Contact popup opens", function (Given, When, Then) {
                Then.onTheProjectsList.onTable().iCheckRows();

                When.onTheProjectsList.iPressManagerContactLink("firstName2 lastName2 (I123)");
                Then.onTheProjectsList.iSeeContactPopoverEmail("email.1@example.net");
                When.onTheProjectsList.iCloseContactPopover();
            });

            opaTest("#3: ListReport: Check actions: Delete shouldn't be enabled", function (Given, When, Then) {
                Then.onTheProjectsList.onTable().iCheckCreate({ visible: false });
                Then.onTheProjectsList.onTable().iCheckDelete({ visible: true });
                Then.onTheProjectsList.onTable().iCheckDelete({ enabled: false });
                Then.onTheProjectsList.onTable().iCheckAction("Edit", { visible: false });
                Then.onTheProjectsList.onTable().iCheckAction("Change Status", { visible: false });
            });

            opaTest("#3: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iSelectRows(2);
                Then.onTheProjectsList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheProjectsList.onTable().iSelectRows(2);
            });

            opaTest("#4 : Should be able to delete ", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iSelectRows(0);
                When.onTheProjectsList.onTable().iExecuteDelete();
                When.onTheProjectsList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project and its history deleted.");
            });

            opaTest("#5 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iSelectRows(0);
                When.onTheProjectsList.onTable().iSelectRows(1);
                Then.onTheProjectsList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheProjectsList.onTable().iExecuteDelete();
                When.onTheProjectsList.onDialog().iConfirm();
                Then.iSeeMessageToast("Projects and their histories deleted.");
            });

            opaTest("#6: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(0);
                Then.onTheProjectsObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#7: Object Page: Check Object Page actions", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(0);
                Then.onTheProjectsObjectPage.iSeeThisPage();
                Then.onTheProjectsObjectPage.onHeader().iCheckDelete({ visible: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckDelete({ enabled: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Edit", { visible: false });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Change Status", { visible: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Change Status", { enabled: true });

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete document", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(0);
                Then.onTheProjectsObjectPage.iSeeThisPage();
                When.onTheProjectsObjectPage.onHeader().iExecuteDelete();
                When.onTheProjectsList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project and its history deleted.");
            });

            opaTest("Teardown", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
