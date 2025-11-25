sap.ui.define([
    'sap/fe/test/ObjectPage',
    'sap/ui/test/actions/EnterText',
    "sap/ui/test/actions/Press",
    "sap/ui/test/Opa5"
], function (ObjectPage, EnterText, Press, Opa5) {
    'use strict';

var isEditable = function (oControl) {
        return oControl.isA && (oControl.isA("sap.m.Input") || oControl.isA("sap.ui.mdc.Field"));
    };

    var matchesPath = function (oControl, sPropertyPath) {
        var aPaths = [];
        var collect = function (sProp) {
            var mInfo = oControl.getBindingInfo && oControl.getBindingInfo(sProp);
            if (!mInfo) { return; }
            var p = mInfo.binding && mInfo.binding.getPath && mInfo.binding.getPath();
            if (p) { aPaths.push(p); }
            if (Array.isArray(mInfo.parts)) {
                mInfo.parts.forEach(function (part) { if (part && part.path) { aPaths.push(part.path); } });
            }
        };
        ["value", "conditions", "text", "selectedKey"].forEach(collect);
        return aPaths.some(function (p) { return p === sPropertyPath || p.endsWith("/" + sPropertyPath); });
    };

    var enterText = function (oControl, sText) {
        var oTarget;
        if (oControl.getContent && typeof oControl.getContent === "function") {
            var aContent = oControl.getContent();
            oTarget = aContent && aContent.length ? aContent[0] : null;
        }
        oTarget = oTarget || oControl;
        new EnterText({ text: sText, clearTextFirst: true }).executeOn(oTarget);
    };

    var CustomPageDefinitions = {
        actions: {
            iEnterTextByProperty: function (sPropertyPath, sText) {
                return this.waitFor({
                    matchers: function (oControl) {
                        return isEditable(oControl) && matchesPath(oControl, sPropertyPath);
                    },
                    success: function (aControls) {
                        var oControl = aControls.find(function (c) { return c.isA && c.isA("sap.ui.mdc.Field"); }) || aControls[0];
                        enterText(oControl, sText);
                        Opa5.assert.ok(true, "Value of " + sPropertyPath + " changed to " + sText);
                    },
                    errorMessage: "Could not enter text value for property \"" + sPropertyPath + "\""
                });
            },

            iPressButtonWithText(text) {
                this.waitFor({
                    controlType: "sap.m.Button",
                    properties: { text: text },
                    actions: new Press(),
                    success: function () {
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
            appId: 'managetype',
            componentId: 'TypeObjectPage',
            contextPath: '/Type'
        },
        CustomPageDefinitions
    );
});