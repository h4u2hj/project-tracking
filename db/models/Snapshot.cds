namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using szakdolgozat.db.models.core.Status from './Status';
using szakdolgozat.db.models.core.Projects from './Projects';

@assert.unique: {nbunique: [
    project,
    createdAt
]}
entity Snapshot : cuid, managed {
    project : Association to one Projects not null;
    status  : Association to one Status not null @Core.Immutable;
}
