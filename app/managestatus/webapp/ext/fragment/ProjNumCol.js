sap.ui.define([
    "sap/base/Log",
    "sap/m/MessageBox"
], function (Log, MessageBox) {
    'use strict';

    var MODULE_ID = "managestatus.ext.fragment.ProjNumCol";

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
         * Project count button event handler
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
            var projectCount = obj.totalProjects;
            var finalStatus = obj.isFinalStatus;

            if (!sID) {
                Log.warning("Status ID missing from binding context", null, MODULE_ID);
                return;
            }

            if (!projectCount) {
                MessageBox.alert("There is no project with this status");
                return;
            }

            getCrossAppNavigation()
                .then(function (oCrossAppNav) {
                    var target, params;
                    if (finalStatus) {
                        target = { semanticObject: "completedprojects", action: "launch" };
                        params = { "status.isFinalStatus": "true", status_ID: sID };
                    } else {
                        target = { semanticObject: "manageprojects", action: "launch" };
                        params = { "status.isFinalStatus": "false", status_ID: sID };
                    }

                    return oCrossAppNav.hrefForExternalAsync({
                        target: target,
                        params: params
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
