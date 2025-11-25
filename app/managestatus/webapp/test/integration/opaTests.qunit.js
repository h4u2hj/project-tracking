sap.ui.require([
    'managestatus/test/integration/pages/JourneyRunner',
    'managestatus/test/integration/FirstJourney',
    'managestatus/test/integration/FilterbarTest'
], function (runner,
    FirstJourney,
    FilterbarTest) {
    'use strict';

    runner.run([FirstJourney.run, FilterbarTest.run]);
});