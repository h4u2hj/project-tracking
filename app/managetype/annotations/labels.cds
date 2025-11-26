using szakdolgozat.srv.service.TypeService as service from '../../../srv/services/TypeService';

annotate service.Type with {
    createdAt               @title: 'Created On';
    createdBy               @title: 'Created By';
    modifiedAt              @title: 'Modified On';
    modifiedBy              @title: 'Modified By';
    name                    @title: 'Type Name';
    totalFinishedProjects   @title: 'Finished Projects';
    totalInProgressProjects @title: 'In-progress Projects'
}
