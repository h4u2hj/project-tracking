namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core} from '../../db/models/';

@path: 'StatusService'
service StatusService {

    entity Status as projection on core.Status;

    @readonly
    entity User   as projection on core.User;

}
