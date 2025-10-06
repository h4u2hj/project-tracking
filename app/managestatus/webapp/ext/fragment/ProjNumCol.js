sap.ui.define([
    "sap/ushell/Container",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Container,
    MessageToast,
    MessageBox) {
    'use strict';

    return {
        /**
         * Generated event handler.
         *
         * @param oEvent the event object provided by the event provider.
         */
        onPress: function (oEvent) {
            var context = oEvent.getSource().getBindingContext();
            var obj = context.getObject();
            var sID = obj.ID;
            var projectCount = obj.totalProjects;
            var finalStatus = obj.isFinalStatus;

            if (projectCount === 0) {
                MessageBox.alert("There is no project with this status");
                return;
            }

            var crossAppNavigatonPromise = Container.getServiceAsync("CrossApplicationNavigation").then(function (oService) {
                if (finalStatus) {
                    return oService.hrefForExternalAsync({
                        target: { semanticObject: "completedprojects", action: "launch" },
                        params: { "status.isFinalStatus": "true", status_ID: sID }
                    });
                }
                else {
                    return oService.hrefForExternalAsync({
                        target: { semanticObject: "manageprojects", action: "launch" },
                        params: { "status.isFinalStatus": "false", status_ID: sID }
                    });
                }
            });
            crossAppNavigatonPromise.then(function (sUrl) {
                if (sUrl) {
                    Container.getServiceAsync("CrossApplicationNavigation").then(function (oCrossAppNavigator) {
                        oCrossAppNavigator.toExternal({
                            target: {
                                shellHash: sUrl
                            }
                        });
                    }).catch(function (error) {
                        console.error("Error getting CrossApplicationNavigation service");
                    });
                }
                else {
                    console.error("Unable to generate url");
                }
            }).catch(function (error) {
                console.error("Error: ", error);
            });
        }
    };
});
