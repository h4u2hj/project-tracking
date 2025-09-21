namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.ProjectService from './ProjectService';

annotate ProjectService with @(
    requires: ['ProjectManager'],
    restrict: [{
        grant: [
            'CREATE',
            'READ',
            'UPDATE'
        ],
        to   : ['ProjectManager']
    }]
);
