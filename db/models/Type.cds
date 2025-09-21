namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using {szakdolgozat.db.models.core.Projects} from './Projects';

@assert.unique: {nbunique: [name]}
entity Type : cuid, managed {
            name      : String(100) not null;
    virtual delete_ac : Boolean;
            projects  : Composition of many Projects
                            on projects.type = $self;

}
