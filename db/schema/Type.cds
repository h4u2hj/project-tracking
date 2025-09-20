namespace szakdolgozat.db.schema;

using {
    cuid,
    managed
} from '@sap/cds/common';

entity Type : cuid, managed {
    name : String(100) not null;
}