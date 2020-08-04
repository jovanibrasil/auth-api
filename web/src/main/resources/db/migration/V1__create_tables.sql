create table users (
	id serial not null,
	user_name varchar(255) not null, 
	email varchar(255) not null, 
	password varchar(255) not null, 
	profile varchar(255) not null, 
	date timestamp not null, 
	primary key (id)
);

create table applications (
	id serial not null, 
	application_type varchar(255) not null,
    	primary key (id)
);

create table registries (
	user_id bigint not null,
	application_id bigint not null,
	registered_at timestamp not null,
    	constraint fk_user foreign key (user_id) references users (id),
    	constraint fk_application foreign key (application_id) references applications (id),
    	primary key (user_id, application_id)
);
