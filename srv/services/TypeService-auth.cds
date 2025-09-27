namespace szakdolgozat.srv.service;

using szakdolgozat.srv.service.TypeService from './TypeService';

annotate TypeService with @(requires: [
    'Administrator',
    'ProjectManager'
]);
