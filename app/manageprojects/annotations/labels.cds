using szakdolgozat.srv.service.ProjectService as service from '../../../srv/services/ProjectService';

annotate service.Projects with {
    completedAt        @title: 'Completed On';
    createdAt          @title: 'Created On';
    createdBy          @title: 'Created By';
    description        @title: 'Description';
    lastStatusChangeAt @title: 'Last Status Change On';
    link               @title: 'Link';
    manager            @title: 'Manager';
    modifiedAt         @title: 'Modified On';
    modifiedBy         @title: 'Modified By';
    name               @title: 'Project Name';
    snapshots          @title: 'Snapshots';
    startDate          @title: 'Started On';
    status             @title: 'Status';
    type               @title: 'Type';
};

annotate service.User with {
    companyID   @title: 'Company ID';
    displayName @title: 'Full Name (Company ID)';
    email       @title: 'Email';
    firstName   @title: 'First Name';
    lastName    @title: 'Last Name';
    phone       @title: 'Phone';
    orgunit     @title: 'Organizational Unit';
    ID          @title: 'Full Name (Company ID)';
}
