create table LOADS
(
    ID            BIGINT auto_increment
        primary key,
    DRONE_ID      BIGINT,
    MEDICATION_ID BIGINT,
    constraint FKC6SH9DHW5CHS1QMBUU4OK61V4
        foreign key (MEDICATION_ID) references MEDICATION,
    constraint FKSD6TJ6LAKONW9D3NN3WSFRMKY
        foreign key (DRONE_ID) references DRONES
);

