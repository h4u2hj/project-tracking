namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.TypeService from './TypeService';

annotate TypeService with @(
    requires: [
        'Administrator',
        'ProjectManager'
    ],
    restrict: [{
        grant: [
            'CREATE',
            'READ',
            'UPDATE',
            'DELETE'
        ],
        to   : [
            'Administrator',
            'ProjectManager'
        ]
    }]
);
