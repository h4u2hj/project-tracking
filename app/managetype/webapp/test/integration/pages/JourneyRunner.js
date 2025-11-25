sap.ui.define([
    "sap/fe/test/JourneyRunner",
    "managetype/test/integration/pages/TypeList",
    "managetype/test/integration/pages/TypeObjectPage"
], function (JourneyRunner, TypeList, TypeObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('managetype') + '/test/flpSandbox.html#managetype-tile',
        pages: {
            onTheTypeList: TypeList,
            onTheTypeObjectPage: TypeObjectPage
        },
        opaConfig: {
            autoWait: true,
            timeout: 10
        },
        async: true
    });

    return runner;
});

