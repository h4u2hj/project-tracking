namespace szakdolgozat.db.schema;

using cuid from '@sap/cds/common';

entity User : cuid {
    email     : String(100) not null;
    firstName : String(100) not null;
    lastName  : String(100) not null;
    phone     : String(15);
    companyID : String(10) not null;
}
