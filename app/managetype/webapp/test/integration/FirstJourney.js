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

                Then.onTheDocTypeList.iSeeThisPage();

            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheDocTypeList.onTable().iCheckRows();
                When.onTheDocTypeList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Should be able to delete and create: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheDocTypeList.onTable().iCheckCreate({ visible: true });
                Then.onTheDocTypeList.onTable().iCheckDelete({ enabled: false });
                Then.onTheDocTypeList.onTable().iCheckAction("Edit", { visible: false });
            });

            opaTest("#3: ListReport: Create new type", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iExecuteCreate();
                Then.onTheDocTypeObjectPage.iSeeThisPage();

                When.onTheDocTypeObjectPage.iEnterTextByLabel("Name", "TestType")


                When.onTheDocTypeObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Document type created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocTypeList.iSeeThisPage();

            });

            opaTest("#4: ListReport: Should be able to select and delete", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iSelectRows(0);
                Then.onTheDocTypeList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocTypeList.onTable().iSelectRows(0);
            });

            opaTest("#5 : Should be able to delete", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iSelectRows(0);
                When.onTheDocTypeList.onTable().iExecuteDelete();
                When.onTheDocTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document type was deleted.");
            });

            opaTest("#6 : Should be able to delete multiple at the same time", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iSelectRows(0);
                When.onTheDocTypeList.onTable().iSelectRows(1);
                Then.onTheDocTypeList.onTable().iCheckDelete({ enabled: true, visible: true });
                When.onTheDocTypeList.onTable().iExecuteDelete();
                When.onTheDocTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document types were deleted.");
            });

            opaTest("#7: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iPressRow(1);
                Then.onTheDocTypeObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheDocTypeList.iSeeThisPage();
            });

            opaTest("#8: Object Page: Delete type", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iPressRow(1);
                Then.onTheDocTypeObjectPage.iSeeThisPage();
                When.onTheDocTypeObjectPage.onHeader().iExecuteDelete();
                When.onTheDocTypeList.onDialog().iConfirm();
                Then.iSeeMessageToast("Document type was deleted.");
            });

            opaTest("#9: Edit status test", function (Given, When, Then) {
                When.onTheDocTypeList.onTable().iPressRow(1);
                Then.onTheDocTypeObjectPage.iSeeThisPage();
                When.onTheDocTypeObjectPage.onHeader().iExecuteAction("Edit")
                When.onTheDocTypeObjectPage.iEnterTextByLabel("Name", "TestName")
                When.onTheDocTypeObjectPage.iPressButtonWithText("Save")

                Then.iSeeMessageToast("Document type saved.");

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });

                Then.onTheDocTypeList.onTable().iCheckRows({ "Name": "TestName" }, 1);
            })

            opaTest("Teardown", function (Given, When, Then) {
                // Cleanup
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});