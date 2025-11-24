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
                Given.iResetMockData({ ServiceUri: "/odata/v4/DocStatusService" })
                    .and.iResetTestData()
                    .and.iStartMyApp();
                Then.onTheDocStatusList.iSeeThisPage();
            });
            
            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheDocStatusList.onTable().iCheckRows();
                When.onTheDocStatusList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check filters default state", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                filterBarActions.iOpenFilterAdaptation()
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "requireProcessor" }, { selected: true })
                    .and.iCheckAdaptationFilterField({ property: "finalState" }, { selected: true })
                    .and.iConfirmFilterAdaptation()
            })

            opaTest("#3: Check modifiedBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableActions = When.onTheDocStatusList.onTable()
                var tableAssertions = Then.onTheDocStatusList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "I234567@sap.com")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "modifiedBy" }, "I234567@sap.com")

                tableActions.iAddAdaptationColumn("Last Changed By")
                tableAssertions.iCheckRows({ "Last Changed By": "Facility Manager (I234567)" }, 3)

                tableActions.iRemoveAdaptationColumn("Last Changed By")
                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
            })

            opaTest("#4: Check modifiedAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableActions = When.onTheDocStatusList.onTable()
                var tableAssertions = Then.onTheDocStatusList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "From Jan 1, 2025")
                    .and.iExecuteSearch()

                tableActions.iAddAdaptationColumn("modifiedAt")
                tableAssertions.iCheckRows({}, 2)

                tableActions.iRemoveAdaptationColumn("modifiedAt")
                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
            })

            opaTest("#5: Check createdBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableActions = When.onTheDocStatusList.onTable()
                var tableAssertions = Then.onTheDocStatusList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "createdBy" }, "I345678@sap.com")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "createdBy" }, "I345678@sap.com")

                tableActions.iAddAdaptationColumn("Created By")
                tableAssertions.iCheckRows({ "Created By": "Recep2 NIst2 (I345678)" }, 3)

                tableActions.iRemoveAdaptationColumn("Created By")
                filterBarActions.iChangeFilterField({ property: "createdBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
            })

            opaTest("#6: Check createdAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableActions = When.onTheDocStatusList.onTable()
                var tableAssertions = Then.onTheDocStatusList.onTable()

                filterBarActions.iAddAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: true })

                tableActions.iAddAdaptationColumn("createdAt")

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "From Jan 1, 2025")
                    .and.iExecuteSearch()

                tableAssertions.iCheckRows({}, 2)

                tableActions.iRemoveAdaptationColumn("createdAt")

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "", true)
                    .and.iExecuteSearch()

                filterBarActions.iRemoveAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
            })

            opaTest("#7: Check Requires Approval filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableAssertions = Then.onTheDocStatusList.onTable()

                filterBarActions.iChangeFilterField({ property: "requireProcessor" }, "Yes")
                    .and.iExecuteSearch()

                tableAssertions.iCheckRows({ "Requires Approval": "Yes" }, 4)

                filterBarActions.iChangeFilterField({ property: "requireProcessor" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#8: Check final state filter", function (Given, When, Then) {
                var filterBarActions = When.onTheDocStatusList.onFilterBar()
                var filterBarAssertions = Then.onTheDocStatusList.onFilterBar()

                var tableAssertions = Then.onTheDocStatusList.onTable()

                filterBarActions.iChangeFilterField({ property: "finalState" }, "Yes")
                    .and.iExecuteSearch()

                tableAssertions.iCheckRows({ "Final State": "Yes" }, 3)

                filterBarActions.iChangeFilterField({ property: "finalState" }, "", true)
                    .and.iExecuteSearch()
            });

            opaTest("#999: Kill the application", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
