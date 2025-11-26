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
        opaConfig: {
            autoWait: true,
            timeout: 10
        },
        async: true
    });

    return runner;
});

