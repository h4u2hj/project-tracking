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
                Given.iResetMockData({ ServiceUri: "/odata/v4/ProjectService" });
                Given.iResetTestData();
                Given.iStartMyApp();

                Then.onTheProjectsList.iSeeThisPage();

            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                When.onTheProjectsList.onFilterBar().iExecuteSearch();
                Then.onTheProjectsList.onTable().iCheckRows();
            });

            opaTest("#2: ListReport: Check actions: Delete shouldn't be enabled, create should", function (Given, When, Then) {
                Then.onTheProjectsList.onTable().iCheckCreate({ visible: true });
                Then.onTheProjectsList.onTable().iCheckCreate({ enabled: true });
                Then.onTheProjectsList.onTable().iCheckDelete({ visible: false });
                Then.onTheProjectsList.onTable().iCheckAction("Edit", { visible: false });
                Then.onTheProjectsList.onTable().iCheckAction("Change Status", { visible: true, enabled: false });
            });

            opaTest("#3: ListReport: Should be able to select and change status", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iSelectRows(0);
                Then.onTheProjectsList.onTable().iCheckAction("Change Status", { visible: true, enabled: true });
                When.onTheProjectsList.onTable().iSelectRows(0);
            });

            opaTest("#4: ListReport: Create new project", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iExecuteCreate();
                Then.onTheProjectsObjectPage.iSeeThisPage();

                When.onTheProjectsObjectPage.iEnterTextByProperty("statusId", "name-new")
                When.onTheProjectsObjectPage.iEnterTextByLabel("Project Name", "New Name")
                When.onTheProjectsObjectPage.iEnterTextByLabel("Type", "TypeName")
                When.onTheProjectsObjectPage.iEnterTextByLabel("Manager", "firstName2 lastName2 (I123)")
                When.onTheProjectsObjectPage.iEnterTextByLabel("Description", "newdesc")

                When.onTheProjectsObjectPage.iPressButtonWithText("Create")
                Then.iSeeMessageToast("Project created.");
                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
                Then.onTheProjectsList.onTable().iCheckRows({}, 3)

                Then.onTheProjectsList.onTable().iCheckRows({ "Project Name": "New Name" }, 1);
                Then.onTheProjectsList.onTable().iCheckRows({ "Description": "newdesc" }, 1);
            });

            opaTest("#5: Object Page: Check Object Page loads", function (Given, When, Then) {
                When.onTheProjectsList.onTable().iPressRow(1);
                Then.onTheProjectsObjectPage.iSeeThisPage();

                When.waitFor({
                    success: function () {
                        sap.ui.test.Opa5.getWindow().history.go(-1);
                    }
                });
                Then.onTheProjectsList.iSeeThisPage();
            });

            opaTest("#6: Object Page: Check Object Page actions", function (Given, When, Then) {
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

            opaTest("#7: Edit project test", function (Given, When, Then) {
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
                Opa5.assert.ok(true, "App teared down");
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});