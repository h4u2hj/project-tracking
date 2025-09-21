namespace szakdolgozat.db.models.core;

using {
    cuid,
    managed
} from '@sap/cds/common';

using szakdolgozat.db.models.core.Status from './Status';
using szakdolgozat.db.models.core.Type from './Type';
using szakdolgozat.db.models.core.User from './User';
using szakdolgozat.db.models.core.Snapshot from './Snapshot';

entity Projects : cuid, managed {
    name               : String(100) not null;
    description        : String(500);
    link               : String(255);
    startDate          : Date      @cds.on.insert: $now;
    status             : Association to Status not null;
    type               : Association to Type not null;
    manager            : Association to User;
    lastStatusChangeAt : Timestamp @cds.on.insert: $now;
    completedAt        : Timestamp;
    snapshots          : Composition of many Snapshot
                             on snapshots.project = $self;
}
