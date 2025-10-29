sap.ui.define(['sap/fe/test/ListReport'], function (ListReport) {
    'use strict';

    var CustomPageDefinitions = {
        actions: {
            iPressButtonWithText(text) {
                this.waitFor({
                    controlType: "sap.m.Button",
                    properties: {
                        text: text
                    },
                    actions: new Press(),
                    success: function (oButton) {
                        Opa5.assert.ok(true, `${text} button pressed`);
                    },
                    errorMessage: `${text} button not found`
                })
            }
        },
        assertions: {}
    };

    return new ListReport(
        {
            appId: 'manageprojects',
            componentId: 'ProjectsList',
            contextPath: '/Projects'
        },
        CustomPageDefinitions
    );
});