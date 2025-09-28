sap.ui.define(['sap/fe/test/ObjectPage'], function(ObjectPage) {
    'use strict';

    var CustomPageDefinitions = {
        actions: {},
        assertions: {}
    };

    return new ObjectPage(
        {
            appId: 'managestatus',
            componentId: 'StatusObjectPage',
            contextPath: '/Status'
        },
        CustomPageDefinitions
    );
});