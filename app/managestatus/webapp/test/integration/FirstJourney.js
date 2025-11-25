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
                Given.iResetMockData({ ServiceUri: "/odata/v4/StatusService" });
                Given.iResetTestData();
                Given.iStartMyApp();

                Then.onTheStatusList.iSeeThisPage();

            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheStatusList.onTable().iCheckRows();
                When.onTheStatusList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check actions: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheStatusList.onTable().iCheckCreate({ visible: true });
                Then.onTheStatusList.onTable().iCheckDelete({ enabled: false });
                Then.onTheStatusList.onTable().iCheckAction("Edit", { visible: false });
            });

            opaTest("#3: ListReport: Create new status", function (Given, When, Then) {
                When.onTheStatusList.onTable().iExecuteCreate();
                Then.onTheStatusObjectPage.iSeeThisPage();

                When.onTheStatusObjectPage.iEnterTextByProperty("name", "teststatus")

                When.onTheStatusObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Project status created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheStatusList.iSeeThisPage();
                Then.onTheStatusList.onTable().iCheckRows({}, 5);

            });

            opaTest("#4: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheStatusList.onTable().iSelectRows(1);
                Then.onTheStatusList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheStatusList.onTable().iSelectRows(1);
            });

            opaTest("#5 : Should be able to delete", function (Given, When, Then) {
                When.onTheStatusList.onTable().iSelectRows(1);
                When.onTheStatusList.onTable().iExecuteDelete();
                When.onTheStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project status was deleted.");
            });

            opaTest("#6 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheStatusList.onTable().iSelectRows(0);
                When.onTheStatusList.onTable().iSelectRows(1);
                Then.onTheStatusList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheStatusList.onTable().iExecuteDelete();
                When.onTheStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project statuses were deleted.");
            });

            opaTest("#7: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheStatusList.onTable().iPressRow(0);
                Then.onTheStatusObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheStatusList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete status", function (Given, When, Then) {
                When.onTheStatusList.onTable().iPressRow(0);
                Then.onTheStatusObjectPage.iSeeThisPage();
                When.onTheStatusObjectPage.onHeader().iExecuteDelete();
                When.onTheStatusList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project status was deleted.");
            });

            opaTest("#9: Edit status test", function (Given, When, Then) {
                When.onTheStatusList.onTable().iPressRow(0);
                Then.onTheStatusObjectPage.iSeeThisPage();
                When.onTheStatusObjectPage.onHeader().iExecuteAction("Edit")
                When.onTheStatusObjectPage.iEnterTextByProperty("name", "TestName")
                When.onTheStatusObjectPage.iPressButtonWithText("Save")

                Then.iSeeMessageToast("Project status saved.");

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });

                When.onTheStatusList.onTable().iSelectRows(0);
                Then.onTheStatusList.onTable().iCheckRows({ "Status Name": "TestName" }, 1);
            })

            opaTest("Teardown", function (Given, When, Then) {
                // Cleanup
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
