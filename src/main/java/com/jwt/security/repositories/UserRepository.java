package com.jwt.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.security.entities.User;

@Transactional(readOnly=true)
@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findUserByUserName(String userName);
	User findUserByEmail(String email);
	User findUserById(Long id);
}
