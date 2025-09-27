namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core} from '../../db/models/';

@path: 'ProjectService'
service ProjectService {
            @(restrict: [{
        grant: [
            'CREATE',
            'READ',
            'UPDATE'
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

annotate ProjectService.Projects with @(
    UI.SelectionVariant   : {SelectOptions: [{
        PropertyName: status.isFinalStatus,
        Ranges      : [{
            Sign  : #I,
            Option: #EQ,
            Low   : false,
        }]
    }]},
    UI.PresentationVariant: {RequestAtLeast: [status.isFinalStatus]}
);
