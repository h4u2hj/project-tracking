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
                Given.iResetMockData({ ServiceUri: "/odata/v4/TypeService" })
                    .and.iResetTestData()
                    .and.iStartMyApp();

                Then.onTheTypeList.iSeeThisPage();
            });

            opaTest("#1: ListReport: Check List Report Page loads and has rows", function (Given, When, Then) {
                Then.onTheTypeList.onTable().iCheckRows();
                When.onTheTypeList.onFilterBar().iExecuteSearch();
            });

            opaTest("#2: ListReport: Check filters default state", function (Given, When, Then) {
                var filterBarActions = When.onTheTypeList.onFilterBar()
                var filterBarAssertions = Then.onTheTypeList.onFilterBar()

                filterBarActions.iOpenFilterAdaptation()
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
                    .and.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
                    .and.iConfirmFilterAdaptation()
            })

            opaTest("#3: Check modifiedBy filter", function (Given, When, Then) {
                var filterBarActions = When.onTheTypeList.onFilterBar()
                var filterBarAssertions = Then.onTheTypeList.onFilterBar()

                var tableActions = When.onTheTypeList.onTable()
                var tableAssertions = Then.onTheTypeList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "email.2@example.net")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "modifiedBy" }, "email.2@example.net")

                tableActions.iAddAdaptationColumn("Modified By")
                tableAssertions.iCheckRows({ "Modified By": "email.2@example.net" }, 2)

                tableActions.iRemoveAdaptationColumn("Modified By")
                filterBarActions.iChangeFilterField({ property: "modifiedBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "modifiedBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedBy" }, { selected: false })
            })

            opaTest("#4: Check modifiedAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheTypeList.onFilterBar()
                var filterBarAssertions = Then.onTheTypeList.onFilterBar()

                var tableActions = When.onTheTypeList.onTable()
                var tableAssertions = Then.onTheTypeList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "modifiedAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "modifiedAt" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "modifiedAt" }, "From Jan 1, 2022")
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
                var filterBarActions = When.onTheTypeList.onFilterBar()
                var filterBarAssertions = Then.onTheTypeList.onFilterBar()

                var tableActions = When.onTheTypeList.onTable()
                var tableAssertions = Then.onTheTypeList.onTable()


                filterBarActions.iAddAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: true })

                filterBarActions.iChangeFilterField({ property: "createdBy" }, "email.2@example.net")
                    .and.iExecuteSearch()
                filterBarAssertions.iCheckFilterField({ property: "createdBy" }, "email.2@example.net")

                tableActions.iAddAdaptationColumn("Created By")
                tableAssertions.iCheckRows({ "Created By": "email.2@example.net" }, 2)

                tableActions.iRemoveAdaptationColumn("Created By")
                filterBarActions.iChangeFilterField({ property: "createdBy" }, "", true)
                    .and.iExecuteSearch()
                filterBarActions.iRemoveAdaptationFilterField({ property: "createdBy" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdBy" }, { selected: false })
            })

            opaTest("#6: Check createdAt filter", function (Given, When, Then) {
                var filterBarActions = When.onTheTypeList.onFilterBar()
                var filterBarAssertions = Then.onTheTypeList.onFilterBar()

                var tableActions = When.onTheTypeList.onTable()
                var tableAssertions = Then.onTheTypeList.onTable()

                filterBarActions.iAddAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: true })

                tableActions.iAddAdaptationColumn("createdAt")

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "From Jan 1, 2022")
                    .and.iExecuteSearch()

                tableAssertions.iCheckRows({}, 1)

                tableActions.iRemoveAdaptationColumn("createdAt")

                filterBarActions.iChangeFilterField({ property: "createdAt" }, "", true)
                    .and.iExecuteSearch()

                filterBarActions.iRemoveAdaptationFilterField({ property: "createdAt" })
                filterBarAssertions.iCheckAdaptationFilterField({ property: "createdAt" }, { selected: false })
            })

            opaTest("#999: Kill the application", function (Given, When, Then) {
                Given.iTearDownMyApp();
            });
        }
    }

    return Journey;
});
