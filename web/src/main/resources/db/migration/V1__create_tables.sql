create table users (
	id bigint not null AUTO_INCREMENT,
	user_name varchar(255) not null, 
	email varchar(255) not null, 
	password varchar(255) not null, 
	profile varchar(255) not null, 
	date datetime not null, 
	primary key (id)
) engine=MyISAM;

create table applications (
	id bigint not null AUTO_INCREMENT, 
	application_type varchar(255) not null,
    primary key (id)
) engine=MyISAM;

create table registries (
	user_id bigint not null,
	application_id bigint not null,
	registered_at datetime not null,
    foreign key (user_id) references users (id),
    foreign key (application_id) references applications (id),
    primary key (user_id, application_id)
) engine=MyISAM;
