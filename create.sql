create table applications (
application_id bigint not null auto_increment,
application integer not null, 
primary key (application_id)) engine=MyISAM

create table registry (

application_id bigint not null, 
user_id bigint not null, 
primary key (application_id, user_id)) engine=MyISAM

create table users (user_id bigint not null auto_increment, 
email varchar(255) not null, 
password varchar(255) not null, 
profile varchar(255) not null, 
date datetime not null, 
user_name varchar(255) not null, 
primary key (user_id)) engine=MyISAM



alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
create table applications (application_id bigint not null auto_increment, application integer not null, primary key (application_id)) engine=MyISAM
create table registry (application_id bigint not null, user_id bigint not null, primary key (application_id, user_id)) engine=MyISAM
create table users (user_id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, profile varchar(255) not null, date datetime not null, user_name varchar(255) not null, primary key (user_id)) engine=MyISAM
alter table registry add constraint FK8aihgcsqfbt5qbxgxwdj2psmg foreign key (application_id) references applications (application_id)
alter table registry add constraint FK4bskb4ql8ps99sw7n2mghdq21 foreign key (user_id) references users (user_id)
