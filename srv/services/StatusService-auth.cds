namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.StatusService from './StatusService';

annotate StatusService with @(
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
