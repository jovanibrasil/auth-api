create table applications (id bigint not null, application_name integer not null) engine=MyISAM
create table users (id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (id)) engine=MyISAM
alter table applications add constraint FKl56i9v2inmlp906jv6epxmk1s foreign key (id) references users (id)
