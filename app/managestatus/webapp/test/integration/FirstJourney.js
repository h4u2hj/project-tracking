sap.ui.define([
    "sap/ui/test/opaQunit",
    "sap/ui/test/Opa5",
    "./pages/JourneyRunner"
], function (opaTest, Opa5, runner) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Basic operations test");

            opaTest("Start application", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/DocStatusService" });
                Given.iResetTestData();
                Given.iStartMyApp();

                Then.onTheDocStatusList.iSeeThisPage();

            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheDocStatusList.onTable().iCheckRows();
                When.onTheDocStatusList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Should be able to delete and create: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheDocStatusList.onTable().iCheckCreate({ visible: true });
                Then.onTheDocStatusList.onTable().iCheckDelete({ enabled: false });
                Then.onTheDocStatusList.onTable().iCheckAction("Edit", { visible: false });
            });

            opaTest("#3: ListReport: Create new status", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iExecuteCreate();
                Then.onTheDocStatusObjectPage.iSeeThisPage();

                When.onTheDocStatusObjectPage.iEnterTextByLabel("Name", "teststatus")


                When.onTheDocStatusObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Document status created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocStatusList.iSeeThisPage();

            });

            opaTest("#4: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iSelectRows(0);
                Then.onTheDocStatusList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocStatusList.onTable().iSelectRows(0);
            });

            opaTest("#5 : Should be able to delete", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iSelectRows(0);
                When.onTheDocStatusList.onTable().iExecuteDelete();
                When.onTheDocStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document status was deleted.");
            });

            opaTest("#6 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iSelectRows(0);
                When.onTheDocStatusList.onTable().iSelectRows(1);
                Then.onTheDocStatusList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocStatusList.onTable().iExecuteDelete();
                When.onTheDocStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document statuses were deleted.");
            });

            opaTest("#7: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iPressRow(1);
                Then.onTheDocStatusObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocStatusList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete status", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iPressRow(1);
                Then.onTheDocStatusObjectPage.iSeeThisPage();
                When.onTheDocStatusObjectPage.onHeader().iExecuteDelete();
                When.onTheDocStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document status was deleted.");
            });

            opaTest("#9: Edit status test", function (Given, When, Then) {
                When.onTheDocStatusList.onTable().iPressRow(1);
                Then.onTheDocStatusObjectPage.iSeeThisPage();
                When.onTheDocStatusObjectPage.onHeader().iExecuteAction("Edit")
                When.onTheDocStatusObjectPage.iEnterTextByLabel("Name", "TestName")
                When.onTheDocStatusObjectPage.iPressButtonWithText("Save")

                Then.iSeeMessageToast("Document status saved.");

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });

                When.onTheDocStatusList.onTable().iSelectRows(1);
                Then.onTheDocStatusList.onTable().iCheckRows({ "Name": "TestName" }, 1);
            })

            opaTest("Teardown", function (Given, When, Then) {
                // Cleanup
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
