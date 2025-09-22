namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core} from '../../db/models/';

@path: 'TypeService'
service TypeService {

    entity Type as projection on core.Type;

    @readonly
    entity User as projection on core.User;

}
