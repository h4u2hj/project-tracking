sap.ui.define([
    "sap/ui/test/opaQunit",
    "sap/ui/test/Opa5",
    "./pages/JourneyRunner"
], function (opaTest, Opa5, runner) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Basic operations test");

            opaTest("#0 Start application", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/CompletedDocService" });
                Given.iResetTestData();
                Given.iStartMyApp();
                Then.onTheDocumentList.iSeeThisPage();
            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheDocumentList.onTable().iCheckRows();
                When.onTheDocumentList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Should be able to delete and edit: Delete shouldn't be enabled", function (Given, When, Then) {
                Then.onTheDocumentList.onTable().iCheckCreate({ visible: false });
                Then.onTheDocumentList.onTable().iCheckDelete({ enabled: false });
                Then.onTheDocumentList.onTable().iCheckAction("Edit", { visible: false });
            });

            opaTest("#3: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iSelectRows(2);
                Then.onTheDocumentList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocumentList.onTable().iSelectRows(2);
            });

            opaTest("#4 : Should be able to delete ", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iSelectRows(0);
                When.onTheDocumentList.onTable().iExecuteDelete();
                When.onTheDocumentList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document and its snapshots deleted.");
            });

            opaTest("#5 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iSelectRows(0);
                When.onTheDocumentList.onTable().iSelectRows(1);
                Then.onTheDocumentList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocumentList.onTable().iExecuteDelete();
                When.onTheDocumentList.onDialog().iConfirm();
                Then.iSeeMessageToast("Documents and their snapshots deleted.");
            });

            opaTest("#6: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iPressRow(1);
                Then.onTheDocumentObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocumentList.iSeeThisPage();
            });

            opaTest("#7: Object Page: Check Object Page actions", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iPressRow(1);
                Then.onTheDocumentObjectPage.iSeeThisPage();
                Then.onTheDocumentObjectPage.onHeader().iCheckDelete({ visible: true });
                Then.onTheDocumentObjectPage.onHeader().iCheckDelete({ enabled: true });
                Then.onTheDocumentObjectPage.onHeader().iCheckAction("Edit", { visible: false });
                Then.onTheDocumentObjectPage.onHeader().iCheckAction("Change Status", { visible: true });
                Then.onTheDocumentObjectPage.onHeader().iCheckAction("Change Status", { enabled: true });

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocumentList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete document", function (Given, When, Then) {
                When.onTheDocumentList.onTable().iPressRow(1);
                Then.onTheDocumentObjectPage.iSeeThisPage();
                When.onTheDocumentObjectPage.onHeader().iExecuteDelete();
                When.onTheDocumentList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document and its snapshots deleted.");
            });

            opaTest("Teardown", function (Given, When, Then) {
                // Cleanup
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
