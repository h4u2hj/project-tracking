sap.ui.define([
    "sap/ui/test/opaQunit",
    "sap/ui/test/Opa5",
    "./pages/JourneyRunner"
], function (opaTest, Opa5, runner) {
    "use strict";

    var Journey = {
        run: function () {
            QUnit.module("Filter fields tests");

            opaTest("#0: Start the application - Filter bar tests ", function (Given, When, Then) {
                Given.iResetMockData({ ServiceUri: "/odata/v4/ProjectService" });
                Given.iResetTestData();
                Given.iStartMyApp();

                Then.onTheProjectsList.iSeeThisPage();
            });
            // @ts-ignore
            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheProjectsList.onTable().iCheckRows();
                When.onTheProjectsList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check filters default state", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                filterBarActions.iOpenFilterAdaptation()
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "manager_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "type_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "status_ID" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "startDate" }, { selected: true })
                    .and.iConfirmFilterAdaptation()
            })

            opaTest("#3: Check modifiedBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableActions = When.onTheProjectsList.onTable()
                var tableAssertions = Then.onTheProjectsList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "email.2@example.net")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "modifiedBy" }, "email.2@example.net")

                tableActions.iAddAdaptationColumn("Modified By")
                tableAssertions.iCheckRows({ "Modified By": "email.2@example.net" }, 1)

                tableActions.iRemoveAdaptationColumn("Modified By")
                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
            })

            opaTest("#4: Check modifiedAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableActions = When.onTheProjectsList.onTable()
                var tableAssertions = Then.onTheProjectsList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "From Jan 1, 2022")
                    .and.iExecuteSearch()

                tableActions.iAddAdaptationColumn("modifiedAt")
                tableAssertions.iCheckRows({}, 1)

                tableActions.iRemoveAdaptationColumn("modifiedAt")
                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
            })

            opaTest("#5: Check createdBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableActions = When.onTheProjectsList.onTable()
                var tableAssertions = Then.onTheProjectsList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "createdBy" }, "email.1@example.net")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "createdBy" }, "email.1@example.net")

                tableActions.iAddAdaptationColumn("Created By")
                tableAssertions.iCheckRows({ "Created By": "email.1@example.net" }, 1)

                tableActions.iRemoveAdaptationColumn("Created By")
                filterBarActions.iChangeFilterField({ property: "createdBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
            })

            opaTest("#6: Check createdAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableActions = When.onTheProjectsList.onTable()
                var tableAssertions = Then.onTheProjectsList.onTable()

                filterBarActions.iAddAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: true })
                filterBarActions.iChangeFilterField({ property: "createdAt" }, "From Jan 1, 2002")
                    .and.iExecuteSearch()

                tableActions.iAddAdaptationColumn("createdAt")
                tableAssertions.iCheckRows({}, 1)

                tableActions.iRemoveAdaptationColumn("createdAt")
                filterBarActions.iChangeFilterField({ property: "createdAt" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
            })

            opaTest("#7: Check manager filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableAssertions = Then.onTheProjectsList.onTable()

                filterBarActions.iChangeFilterField({ property: "manager_ID" }, "firstName1 lastName1 (D107)")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "manager_ID" }, "firstName1 lastName1 (D107)")
                tableAssertions.iCheckRows({ "Manager": "firstName1 lastName1 (D107)" }, 1)

                filterBarActions.iChangeFilterField({ property: "manager_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#8: Check type filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableAssertions = Then.onTheProjectsList.onTable()

                filterBarActions.iChangeFilterField({ property: "type_ID" }, "TypeName")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "type_ID" }, "TypeName")
                tableAssertions.iCheckRows({ "Type": "TypeName" }, 2)

                filterBarActions.iChangeFilterField({ property: "type_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#9: Check status filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableAssertions = Then.onTheProjectsList.onTable()

                filterBarActions.iChangeFilterField({ property: "status_ID" }, "name-new")
                    .and.iExecuteSearch()

                filterBarAssertions.iCheckFilterField({ property: "status_ID" }, "name-new")
                tableAssertions.iCheckRows({ "Status": "name-new" }, 2)

                filterBarActions.iChangeFilterField({ property: "status_ID" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#10: Check started on filter", function (Given, When, Then) {
                var filterBarActions = When.onTheProjectsList.onFilterBar()
                var filterBarAssertions = Then.onTheProjectsList.onFilterBar()

                var tableAssertions = Then.onTheProjectsList.onTable()

                filterBarActions.iChangeFilterField({ property: "startDate" }, "Mar 24, 2017")
                    .and.iExecuteSearch()

                //filterBarAssertions.iCheckFilterField({ property: "startDate" }, "Mar 24, 2017")
                tableAssertions.iCheckRows({}, 1)

                filterBarActions.iChangeFilterField({ property: "startDate" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#999: Kill the application", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
