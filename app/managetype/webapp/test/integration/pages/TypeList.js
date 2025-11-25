sap.ui.define([
    'sap/fe/test/ListReport',
    "sap/ui/test/actions/Press",
    "sap/ui/test/Opa5"
], function (ListReport, Press, Opa5) {
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
            appId: 'managetype',
            componentId: 'TypeList',
            contextPath: '/Type'
        },
        CustomPageDefinitions
    );
});