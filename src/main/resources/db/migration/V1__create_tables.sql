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
	id bigint not null, 
	application_name varchar(255) not null,
    foreign key (id) references users (id)
) engine=MyISAM;
