namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using {szakdolgozat.db.models.core.Projects} from './Projects';

@assert.unique: {nbunique: [name]}
entity Status : cuid, managed {
            name          : String(100) not null;
            isFinalStatus : Boolean not null default false;
    virtual delete_ac     : Boolean;
            projects      : Composition of many Projects
                                on projects.status = $self;
}
