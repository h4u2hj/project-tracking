sap.ui.define([
    "sap/ui/test/opaQunit",
    "sap/ui/test/Opa5"
], function (opaTest, Opa5) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Basic operations test");

            opaTest("Start application", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/ProjectService" });
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

            opaTest("#3: ListReport: Check actions: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheProjectsList.onTable().iCheckCreate({ visible: true });
                Then.onTheProjectsList.onTable().iCheckCreate({ enabled: true });
                Then.onTheProjectsList.onTable().iCheckDelete({ visible: false });
                Then.onTheProjectsList.onTable().iCheckAction("Edit", { visible: false });
                Then.onTheProjectsList.onTable().iCheckAction("Change Status", { visible: true, enabled: false });
            });

            opaTest("#4: ListReport: Should be able to select and change status", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iSelectRows(0);
                Then.onTheProjectsList.onTable().iCheckAction("Change Status", { visible: true, enabled: true });
                When.onTheProjectsList.onTable().iSelectRows(0);
            });

            opaTest("#5: ListReport: Create new project", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iExecuteCreate();
                Then.onTheProjectsObjectPage.iSeeThisPage();

                When.onTheProjectsObjectPage.iEnterTextByProperty("name", "New Name")
                When.onTheProjectsObjectPage.iEnterTextByProperty("status_ID", "name-new")
                When.onTheProjectsObjectPage.iEnterTextByProperty("type_ID", "TypeName")
                When.onTheProjectsObjectPage.iEnterTextByProperty("manager_ID", "firstName2 lastName2 (I123)")
                When.onTheProjectsObjectPage.iEnterTextByProperty("description", "newdesc")

                When.onTheProjectsObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Project created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
                Then.onTheProjectsList.onTable().iCheckRows({}, 3)

                Then.onTheProjectsList.onTable().iCheckRows({ "Project Name": "New Name" }, 1);
                Then.onTheProjectsList.onTable().iCheckRows({ "Description": "newdesc" }, 1);
            });

            opaTest("#6: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(1);
                Then.onTheProjectsObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#7: Object Page: Check Object Page actions", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(1);
                Then.onTheProjectsObjectPage.iSeeThisPage();
                Then.onTheProjectsObjectPage.onHeader().iCheckDelete({ visible: false });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Edit", { visible: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Edit", { enabled: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Change Status", { visible: true });
                Then.onTheProjectsObjectPage.onHeader().iCheckAction("Change Status", { enabled: true });

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#8: Edit project test", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(1);
                Then.onTheProjectsObjectPage.iSeeThisPage();
                When.onTheProjectsObjectPage.onHeader().iExecuteAction("Edit")
                When.onTheProjectsObjectPage.iEnterTextByProperty("name", "Updated name")
                When.onTheProjectsObjectPage.iEnterTextByProperty("description", "newdescription")
                When.onTheProjectsObjectPage.iPressButtonWithText("Save")

                Then.iSeeMessageToast("Project saved.");

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.back();
                    }
                });

                Then.onTheProjectsList.onTable().iCheckRows({ "Project Name": "Updated name" }, 1);
                Then.onTheProjectsList.onTable().iCheckRows({ "Description": "newdescription" }, 1);
            })

            opaTest("Teardown", function (Given, When, Then) {
                Given.iTearDownMyApp();
                Opa5.assert.ok(true, "App teared down");
            });
        }
    }

    return Journey;
});
