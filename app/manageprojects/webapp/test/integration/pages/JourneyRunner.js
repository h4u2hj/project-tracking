sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"manageprojects/test/integration/pages/ProjectsList",
	"manageprojects/test/integration/pages/ProjectsObjectPage"
], function (JourneyRunner, ProjectsList, ProjectsObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('manageprojects') + '/test/flpSandbox.html#manageprojects-tile',
        pages: {
			onTheProjectsList: ProjectsList,
			onTheProjectsObjectPage: ProjectsObjectPage
        },
        async: true
    });

    return runner;
});

