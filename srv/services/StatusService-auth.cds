namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.StatusService from './StatusService';

annotate StatusService with @(requires: [
    'Administrator',
    'ProjectManager'
]);
