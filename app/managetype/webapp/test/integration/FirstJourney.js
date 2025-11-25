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
                Given.iResetMockData({ ServiceUri: "/odata/v4/TypeService" });
                Given.iResetTestData();
                Given.iStartMyApp();

                Then.onTheTypeList.iSeeThisPage();

            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheTypeList.onTable().iCheckRows();
                When.onTheTypeList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check actions: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheTypeList.onTable().iCheckCreate({ visible: true });
                Then.onTheTypeList.onTable().iCheckDelete({ enabled: false });
                Then.onTheTypeList.onTable().iCheckAction("Edit", { visible: false });
            });

            opaTest("#3: ListReport: Create new type", function (Given, When, Then) {
                When.onTheTypeList.onTable().iExecuteCreate();
                Then.onTheTypeObjectPage.iSeeThisPage();

                When.onTheTypeObjectPage.iEnterTextByProperty("name", "TestType")

                When.onTheTypeObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Project type created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheTypeList.iSeeThisPage();

                Then.onTheTypeList.onTable().iCheckRows({}, 5);

            });

            opaTest("#4: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheTypeList.onTable().iSelectRows(1);
                Then.onTheTypeList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheTypeList.onTable().iSelectRows(1);
            });

            opaTest("#5 : Should be able to delete", function (Given, When, Then) {
                When.onTheTypeList.onTable().iSelectRows(2);
                When.onTheTypeList.onTable().iExecuteDelete();
                When.onTheTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project type was deleted.");
            });

            opaTest("#6 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheTypeList.onTable().iSelectRows(0);
                When.onTheTypeList.onTable().iSelectRows(1);
                Then.onTheTypeList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheTypeList.onTable().iExecuteDelete();
                When.onTheTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project types were deleted.");
            });

            opaTest("#7: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheTypeList.onTable().iPressRow(0);
                Then.onTheTypeObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheTypeList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete type", function (Given, When, Then) {
                When.onTheTypeList.onTable().iPressRow(0);
                Then.onTheTypeObjectPage.iSeeThisPage();
                When.onTheTypeObjectPage.onHeader().iExecuteDelete();
                When.onTheTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Project type was deleted.");
            });

            opaTest("#9: Edit status test", function (Given, When, Then) {
                When.onTheTypeList.onTable().iPressRow(0);
                Then.onTheTypeObjectPage.iSeeThisPage();
                When.onTheTypeObjectPage.onHeader().iExecuteAction("Edit")
                When.onTheTypeObjectPage.iEnterTextByProperty("name", "TestName12")
                When.onTheTypeObjectPage.iPressButtonWithText("Save")

                Then.iSeeMessageToast("Project type saved.");

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });

                Then.onTheTypeList.onTable().iCheckRows({ "Type Name": "TestName12" }, 1);
            })

            opaTest("Teardown", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});