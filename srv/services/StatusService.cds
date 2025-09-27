namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core} from '../../db/models/';

@path: 'StatusService'
service StatusService {
    @(restrict: [{
        grant: [
            'CREATE',
            'READ',
            'UPDATE',
            'DELETE'
        ],
        to   : [
            'Administrator',
            'ProjectManager'
        ]
    }])
    entity Status as projection on core.Status;

    @readonly
    entity User   as projection on core.User;

}
