sap.ui.require([
    'manageprojects/test/integration/pages/JourneyRunner',
    'manageprojects/test/integration/FirstJourney',
    'manageprojects/test/integration/FilterbarTest'
], function (runner,
    FirstJourney,
    FilterbarTest) {
    'use strict';

    runner.run([FirstJourney.run, FilterbarTest.run]);
});
