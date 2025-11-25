sap.ui.require([
    'managetype/test/integration/pages/JourneyRunner',
    'managetype/test/integration/FirstJourney',
    'managetype/test/integration/FilterbarTest'
], function (runner,
    FirstJourney,
    FilterbarTest) {
    'use strict';

    runner.run([FirstJourney.run, FilterbarTest.run]);
});
