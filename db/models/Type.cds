namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using {szakdolgozat.db.models.core.Project} from './Project';

@assert.unique: {nbunique: [name]}
entity Type : cuid, managed {
            name      : String(100) not null @mandatory;
    virtual delete_ac : Boolean;
            projects  : Composition of many Project
                            on projects.type = $self;

}
