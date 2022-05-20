CREATE TABLE room (
    id BIGINT auto_increment NOT NULL,
    name varchar(20) NOT NULL,
    seats INT NOT NULL,
    active TINYINT NOT NULL,
    PRIMARY KEY(id)
);