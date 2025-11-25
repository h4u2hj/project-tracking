sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"managestatus/test/integration/pages/StatusList",
	"managestatus/test/integration/pages/StatusObjectPage"
], function (JourneyRunner, StatusList, StatusObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('managestatus') + '/test/flpSandbox.html#managestatus-tile',
        pages: {
			onTheStatusList: StatusList,
			onTheStatusObjectPage: StatusObjectPage
        },
        opaConfig: {
            autoWait: true,
            timeout: 10
        },
        async: true
    });

    return runner;
});

