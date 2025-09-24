namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.CompletedProjectService from './CompletedProjectService';

annotate CompletedProjectService with @(
    requires: ['ProjectManager'],
    restrict: [{
        grant: [
            'READ',
            'DELETE'
        ],
        to   : ['ProjectManager']
    }]
);
