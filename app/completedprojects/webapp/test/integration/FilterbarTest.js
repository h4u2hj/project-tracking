sap.ui.define([
    "sap/ui/test/opaQunit",
    "sap/ui/test/Opa5",
    "./pages/JourneyRunner"
], function (opaTest, Opa5, runner) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Basic operations test");

            opaTest("#0: Start the application - Filter bar tests ", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/CompletedDocService" })
                    .and.iResetTestData()
                    .and.iStartMyApp();
                Then.onTheDocumentList.iSeeThisPage();
            });
            // @ts-ignore
            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheDocumentList.onTable().iCheckRows();
                When.onTheDocumentList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check filters default state", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                filterBarActions.iOpenFilterAdaptation()
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "completedAt" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "employee_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "currentProcessor_ID" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "type_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "status_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "effectiveDate" }, { selected: true })
                    .and.iConfirmFilterAdaptation()
            })

            opaTest("#3: Check modifiedBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableActions = When.onTheDocumentList.onTable()
                var tableAssertions = Then.onTheDocumentList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "I333333@sap.com")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "modifiedBy" }, "I333333@sap.com")

                tableActions.iAddAdaptationColumn("Last Changed By")
                tableAssertions.iCheckRows({ "Last Changed By": "David Brown (I333333)" }, 5)

                tableActions.iRemoveAdaptationColumn("Last Changed By")
                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
            })

            opaTest("#4: Check modifiedAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableActions = When.onTheDocumentList.onTable()
                var tableAssertions = Then.onTheDocumentList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "From Jan 1, 2025")
                    .and.iExecuteSearch()

                tableActions.iAddAdaptationColumn("modifiedAt")
                tableAssertions.iCheckRows({}, 3)

                tableActions.iRemoveAdaptationColumn("modifiedAt")
                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
            })

            opaTest("#5: Check createdBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableActions = When.onTheDocumentList.onTable()
                var tableAssertions = Then.onTheDocumentList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "createdBy" }, "I222222@sap.com")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "createdBy" }, "I222222@sap.com")

                tableActions.iAddAdaptationColumn("Created By")
                tableAssertions.iCheckRows({ "Created By": "Mary Johnson (I222222)" }, 1)

                tableActions.iRemoveAdaptationColumn("Created By")
                filterBarActions.iChangeFilterField({ property: "createdBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
            })

            opaTest("#6: Check createdAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableActions = When.onTheDocumentList.onTable()
                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "From Jan 1, 2025")
                    .and.iExecuteSearch()

                //filterBarAssertions.iCheckFilterField({ property: "createdAt" }, "From Jan 1, 2025")
                tableAssertions.iCheckRows({}, 3)

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "", true)
                    .and.iExecuteSearch()
            })

            opaTest("#7: Check employee filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iChangeFilterField({ property: "employee_ID" }, "Michael Jones (I555555)")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "employee_ID" }, "Michael Jones (I555555)")
                tableAssertions.iCheckRows({ "Employee": "Michael Jones (I555555)" }, 5)

                filterBarActions.iChangeFilterField({ property: "employee_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#8: Check type filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iChangeFilterField({ property: "type_ID" }, "Employer Contract")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "type_ID" }, "Employer Contract")
                tableAssertions.iCheckRows({"Type": "Employer Contract"}, 3)

                filterBarActions.iChangeFilterField({ property: "type_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#9: Check status filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iChangeFilterField({ property: "status_ID" }, "Approved")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "status_ID" }, "Approved")
                tableAssertions.iCheckRows({ "Status": "Approved" }, 2)

                filterBarActions.iChangeFilterField({ property: "status_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#10: Check effective from filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()

                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iChangeFilterField({ property: "effectiveDate" }, "Jun 9, 2025")
                    .and.iExecuteSearch()

                //filterBarAssertions.iCheckFilterField({ property: "effectiveDate" }, "Jun 9, 2025")
                tableAssertions.iCheckRows({}, 1)

                filterBarActions.iChangeFilterField({ property: "effectiveDate" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("11: Check processor-completedby filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocumentList.onFilterBar()
                var filterBarAssertions = Then.onTheDocumentList.onFilterBar()
                var tableActions = When.onTheDocumentList.onTable()
                var tableAssertions = Then.onTheDocumentList.onTable()

                filterBarActions.iAddAdaptationFilterField({ property: "currentProcessor_ID" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "currentProcessor_ID" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "currentProcessor_ID" }, "documentmanager DEFAULT (I666666)")
                    .and.iExecuteSearch()

                tableActions.iAddAdaptationColumn("Completed By")

                filterBarAssertions.iCheckFilterField({ property: "currentProcessor_ID" }, "documentmanager DEFAULT (I666666)")
                tableAssertions.iCheckRows({ "Completed By": "documentmanager DEFAULT (I666666)" }, 5)

                tableActions.iRemoveAdaptationColumn("Completed By")
                filterBarActions.iChangeFilterField({ property: "currentProcessor_ID" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "currentProcessor_ID" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "currentProcessor_ID" }, { selected: false })

            });

            opaTest("#999: Kill the application", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
