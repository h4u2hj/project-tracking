namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core} from '../../db/models/';

@path: 'CompletedProjectService'
service CompletedProjectService {
            @(restrict: [{
        grant: [
            'READ',
            'DELETE',
            'changeStatus'
        ],
        to   : ['ProjectManager']
    }])
    entity Projects        as projection on core.Project
        actions {
            @(
                cds.odata.bindingparameter.name: '_it',
                Common.SideEffects             : {TargetEntities: ['_it']}
            )
            action changeStatus(newStatus: UUID, changeDate: Timestamp) returns Projects;
        };

    entity ProjectSnapshot as projection on core.Snapshot;

    @readonly
    entity User            as projection on core.User;

    @readonly
    entity Type            as projection on core.Type;

    @readonly
    entity Status          as projection on core.Status;

};

annotate CompletedProjectService.Projects with @(
    UI.SelectionVariant   : {SelectOptions: [{
        PropertyName: status.isFinalStatus,
        Ranges      : [{
            Sign  : #I,
            Option: #EQ,
            Low   : true,
        }]
    }]},
    UI.PresentationVariant: {RequestAtLeast: [status.isFinalStatus]}
);
