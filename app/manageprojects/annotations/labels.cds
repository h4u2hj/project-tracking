using szakdolgozat.srv.service.ProjectService as service from '../../../srv/services/ProjectService';

annotate service.Projects with {
    completedAt @title: 'Completed On';
    createdAt   @title: 'Created On';

};
