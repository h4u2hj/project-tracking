sap.ui.define([
    'sap/fe/test/ObjectPage',
    'sap/ui/test/actions/EnterText',
    "sap/ui/test/actions/Press",
    "sap/ui/test/Opa5"
], function (ObjectPage, EnterText, Press, Opa5) {
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
                    errorMessage: `Input field for text "${value}" not found`,
                });
            },
            iEnterText: function (sId, sText) {
                return this.waitFor({
                    id: sId,
                    actions: new EnterText({
                        text: sText,
                    }),
                    errorMessage: `Input field for text "${sText}" not found`,
                });
            },
            iEnterTextByProperty: function (sPropertyPath, sText) {
                return this.waitFor({
                    controlType: "sap.m.Input",
                    //viewId: "com.sap.epd.specification.maintainspecification::PropertiesObjectPage",
                    //DATAFIELD_NAME_ID: "com.sap.epd.specification.maintainspecification::SpecificationVersionObjectPage--fe::FormContainer::BasicData::FormElement::DataField::name::Field-edit",
                    bindingPath: {
                        propertyPath: sPropertyPath
                    },
                    actions: new EnterText({
                        text: sText
                    }),
                    errorMessage: "Could not enter text value"
                });
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
            contextPath: '/Projects',
            entity: 'Projects',
            entitySet: 'Projects'
        },
        CustomPageDefinitions
    );
});