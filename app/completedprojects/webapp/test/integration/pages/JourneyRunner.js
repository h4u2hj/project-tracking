sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"completedprojects/test/integration/pages/ProjectsList",
	"completedprojects/test/integration/pages/ProjectsObjectPage"
], function (JourneyRunner, ProjectsList, ProjectsObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('completedprojects') + '/test/flpSandbox.html#completedprojects-tile',
        pages: {
			onTheProjectsList: ProjectsList,
			onTheProjectsObjectPage: ProjectsObjectPage
        },
        async: true
    });

    return runner;
});

