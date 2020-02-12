package com.security.web.repositories;

import com.security.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Transactional(readOnly=true)
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);
	Optional<User> findByEmail(String email);
	Optional<User> findUserById(Long id);
}
