DELIMITER $$
CREATE PROCEDURE insert_usersdb_data() 
BEGIN
	IF (NOT EXISTS(SELECT user_name FROM `usersdb`.`users`)) then
        INSERT INTO `usersdb`.`users` (`user_name`, `email`, `password`, `profile`, `date`)
		VALUES ("user", "user@email.com", "$2a$10$qTtL1uiEaE1rI648DK5vV.Jadtc2KZHHctcoPf.SeHETV5RhfvIS2", "ROLE_USER", NOW());
		
        INSERT INTO `usersdb`.`users` (`user_name`, `email`, `password`, `profile`, `date`)
		VALUES ("admin", "admin@email.com", "$2a$10$qTtL1uiEaE1rI648DK5vV.Jadtc2KZHHctcoPf.SeHETV5RhfvIS2", "ROLE_ADMIN", NOW());

		INSERT INTO `usersdb`.`applications` (`application_type`)
		VALUES ("NOTES_APP");
		
		INSERT INTO `usersdb`.`applications` (`application_type`)
		VALUES ("BLOG_APP");
		
		INSERT INTO `usersdb`.`registries` (`user_id`, `application_id`, `registered_at`)
		VALUES (1, 1, NOW());
		
		INSERT INTO `usersdb`.`registries` (`user_id`, `application_id`, `registered_at`)
		VALUES (2, 2, NOW());
		
	END IF;
END $$
DELIMITER ;

CALL insert_usersdb_data(); /* Insert test data */