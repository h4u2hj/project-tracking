namespace szakdolgozat.db.schema;

using {
    cuid,
    managed
} from '@sap/cds/common';

entity Status : cuid, managed {
    name : String(100) not null;
    isFinalStatus : Boolean not null default false;
}