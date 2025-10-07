using szakdolgozat.srv.service.StatusService as service from '../../../srv/services/StatusService';

annotate service.Status with {
    createdAt @title: 'Created On';
    createdBy @title: 'Created By';
    modifiedAt @title: 'Modified On';
    modifiedBy @title: 'Modified By';
    name @title: 'Status Name';
    isFinalStatus @title: 'Final State';
    totalProjects @title: 'Total Projects';
}
