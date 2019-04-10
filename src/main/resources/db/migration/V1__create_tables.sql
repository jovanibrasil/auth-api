create table users (
	user_name varchar(255) not null, 
	email varchar(255) not null, 
	password varchar(255) not null, 
	profile varchar(255) not null, 
	date datetime not null, 
	primary key (user_name)
) engine=MyISAM;

create table applications (
	user_name varchar(255) not null, 
	application_name varchar(255) not null,
    foreign key (user_name) references users (user_name)
) engine=MyISAM;