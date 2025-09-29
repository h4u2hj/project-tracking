namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using szakdolgozat.db.models.core.Status from './Status';
using szakdolgozat.db.models.core.Type from './Type';
using szakdolgozat.db.models.core.User from './User';
using szakdolgozat.db.models.core.Snapshot from './Snapshot';

entity Project : cuid, managed {
    name               : String(100) not null @mandatory;
    description        : String(500);
    link               : String(255);
    startDate          : Date      @cds.on.insert: $now;
    status             : Association to Status not null @mandatory @assert.target;
    type               : Association to Type not null @mandatory @assert.target;
    manager            : Association to User @mandatory @assert.target;
    lastStatusChangeAt : Timestamp @cds.on.insert: $now;
    completedAt        : Timestamp;
    snapshots          : Composition of many Snapshot
                             on snapshots.project = $self;
}
