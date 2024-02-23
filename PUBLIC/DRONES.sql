create table DRONES
(
    ID BIGINT auto_increment primary key,
    BATTERY_CAPACITY INTEGER,
    MODEL            CHARACTER VARYING(255),
    SERIAL_NUMBER    CHARACTER VARYING(100) constraint UK_LSJVI6H63GA5X3QWVO5VOQ1F8 unique,
    STATE            CHARACTER VARYING(255),
    WEIGHT_LIMIT     INTEGER,
    check ("WEIGHT_LIMIT" <= 500)
);

