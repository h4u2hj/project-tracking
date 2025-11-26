sap.ui.require([
    'completedprojects/test/integration/pages/JourneyRunner',
    'completedprojects/test/integration/FirstJourney',
    'completedprojects/test/integration/FilterbarTest'
], function (runner,
    FirstJourney,
    FilterbarTest) {
    'use strict';

    runner.run([FirstJourney.run, FilterbarTest.run]);
});