sap.ui.define(['sap/fe/test/ObjectPage'], function (ObjectPage) {
    'use strict';

    var CustomPageDefinitions = {
        actions: {
            iEnterTextByLabel: function (label, value) {
                this.waitFor({
                    controlType: "sap.m.Input",
                    ancestor: {
                        controlType: "sap.ui.layout.form.FormElement",
                        descendant: {
                            controlType: "sap.m.Label",
                            properties: {
                                text: label
                            }
                        }
                    },
                    actions: new EnterText({ text: value }),
                    success: function (oButton) {
                        Opa5.assert.ok(true, `Value of ${label} changed to ${value}`);
                    },
                })
            },
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

    return new ObjectPage(
        {
            appId: 'manageprojects',
            componentId: 'ProjectsObjectPage',
            contextPath: '/Projects'
        },
        CustomPageDefinitions
    );
});