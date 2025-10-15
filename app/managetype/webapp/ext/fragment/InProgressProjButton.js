sap.ui.define([
    "sap/base/Log",
    "sap/m/MessageBox"
], function (Log, MessageBox) {
    'use strict';

    var MODULE_ID = "managetype.ext.fragment.InProgressProjButton";

    var getCrossAppNavigation = function () {
        if (sap.ushell && sap.ushell.Container && sap.ushell.Container.getServiceAsync) {
            return sap.ushell.Container.getServiceAsync("CrossApplicationNavigation");
        }

        return new Promise(function (resolve, reject) {
            sap.ui.require(
                ["sap/ushell/Container"],
                function (Container) {
                    if (Container && Container.getServiceAsync) {
                        Container.getServiceAsync("CrossApplicationNavigation").then(resolve, reject);
                    } else {
                        reject(new Error("CrossApplicationNavigation service not available"));
                    }
                },
                reject
            );
        });
    };

    var showNavigationError = function (error) {
        Log.error("Failed to trigger cross-app navigation", error, MODULE_ID);
        MessageBox.error("We could not open the related project list. Please try again later.");
    };

    return {
        /**
         * In-Progress Project count button event handler
         *
         * @param oEvent the event object provided by the event provider.
         */
        onPress: function (oEvent) {
            var context = oEvent.getSource().getBindingContext();
            if (!context) {
                return;
            }

            var obj = context.getObject() || {};
            var sID = obj.ID;
            var projectCount = obj.totalInProgressProjects;

            if (!sID) {
                Log.warning("Type ID missing from binding context", null, MODULE_ID);
                return;
            }

            if (!projectCount) {
                MessageBox.alert("There is no project with this type");
                return;
            }

            getCrossAppNavigation()
                .then(function (oCrossAppNav) {
                    return oCrossAppNav.hrefForExternalAsync({
                        target: { semanticObject: "manageprojects", action: "launch" },
                        params: { "status.isFinalStatus": "false", type_ID: sID }
                    }).then(function (sUrl) {
                        if (!sUrl) {
                            throw new Error("Cross-app hash could not be determined");
                        }
                        oCrossAppNav.toExternal({
                            target: {
                                shellHash: sUrl
                            }
                        });
                    });
                })
                .catch(showNavigationError);
        }
    };
});
