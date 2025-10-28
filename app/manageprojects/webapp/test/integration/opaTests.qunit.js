sap.ui.require([
    'manageprojects/test/integration/pages/JourneyRunner',
    'manageprojects/test/integration/FirstJourney'
], function (runner,
    FirstJourney) {
    'use strict';

    runner.run([FirstJourney.run]);
});