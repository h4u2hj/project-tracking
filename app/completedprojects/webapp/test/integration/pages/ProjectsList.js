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
            },
            iPressManagerContactLink(displayName) {
                return this.waitFor({
                    controlType: "sap.m.Link",
                    properties: {
                        text: displayName
                    },
                    success: function (aLinks) {
                        new Press().executeOn(aLinks[0]);
                        Opa5.assert.ok(true, `Manager contact link '${displayName}' pressed`);
                    },
                    errorMessage: `Manager contact link '${displayName}' not found`
                });
            },
            iCloseContactPopover() {
                return this.waitFor({
                    searchOpenDialogs: true,
                    controlType: "sap.m.Popover",
                    success: function (aPopovers) {
                        if (aPopovers[0]) {
                            aPopovers[0].close();
                        }
                        Opa5.assert.ok(true, "Contact popover closed");
                    },
                    errorMessage: "Contact popover not open to close"
                });
            }
        },
        assertions: {
            iSeeContactPopoverEmail(email) {
                return this.waitFor({
                    searchOpenDialogs: true,
                    controlType: "sap.m.Link",
                    properties: {
                        text: email
                    },
                    success: function () {
                        Opa5.assert.ok(true, `Contact popover shows email ${email}`);
                    },
                    errorMessage: `Contact popover email '${email}' not found`
                });
            }
        }
    };

    return new ListReport(
        {
            appId: 'completedprojects',
            componentId: 'ProjectsList',
            contextPath: '/Projects'
        },
        CustomPageDefinitions
    );
});