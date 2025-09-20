namespace szakdolgozat.db.schema;

using {
    cuid,
    managed
} from '@sap/cds/common';

using szakdolgozat.db.schema.Status from './Status';
using szakdolgozat.db.schema.Type from './Type';
using szakdolgozat.db.schema.User from './User';

entity Projects : cuid, managed {
    name  : String(100) not null;
    description : String(500);
    startDate : Date @cds.on.insert: $now;
    Status : Association to Status;
    Type : Association to Type;
    Manager : Association to User;
}
