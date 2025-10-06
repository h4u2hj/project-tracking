namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using {szakdolgozat.db.models.core.Project} from './Project';

@assert.unique: {nbunique: [name]}
entity Status : cuid, managed {
            name          : String(100) not null;
            isFinalStatus : Boolean not null default false;
    virtual delete_ac     : Boolean;
    virtual totalProjects : Integer default 0;
            projects      : Composition of many Project
                                on projects.status = $self;
}
