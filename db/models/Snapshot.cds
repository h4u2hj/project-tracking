namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using szakdolgozat.db.models.core.Status from './Status';
using szakdolgozat.db.models.core.Project from './Project';

@assert.unique: {nbunique: [
    project,
    createdAt
]}
entity Snapshot : cuid, managed {
    project    : Association to one Project not null  @mandatory       @assert.target;
    status     : Association to one Status not null   @Core.Immutable  @mandatory  @assert.target;
    statusName : String = status.name;
}
