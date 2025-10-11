sap.ui.define([
    "sap/ushell/Container",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], function (Container, MessageToast, MessageBox) {
    'use strict';

    return {
        /**
         * In-Progress Project count button event handler
         *
         * @param oEvent the event object provided by the event provider.
         */
        onPress: function (oEvent) {
            var context = oEvent.getSource().getBindingContext();
            var obj = context.getObject();
            var sID = obj.ID;
            var projectCount = obj.totalInProgressProjects;

            if (projectCount === 0) {
                MessageBox.alert("There is no project with this type");
                return;
            }

            var crossAppNavigatonPromise = Container.getServiceAsync("CrossApplicationNavigation").then(function (oService) {

                return oService.hrefForExternalAsync({
                    target: { semanticObject: "manageprojects", action: "launch" },
                    params: { "status.isFinalStatus": "false", type_ID: sID }
                });

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
                        console.error("Error getting CrossApplicationNavigation service", error);
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
