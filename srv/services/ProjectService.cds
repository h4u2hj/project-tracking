namespace szakdolgozat.srv.service;

using {szakdolgozat.db.models.core  } from '../../db/models/';

@path: 'ProjectService'
service ProjectService {

    entity Projects as projection on core.Projects;
    entity ProjectSnapshot as projection on core.Snapshot;

}