create table MEDICATION
(
    ID     BIGINT auto_increment
        primary key,
    CODE   CHARACTER VARYING(255),
    IMAGE  BINARY LARGE OBJECT,
    NAME   CHARACTER VARYING(255),
    WEIGHT INTEGER
);

