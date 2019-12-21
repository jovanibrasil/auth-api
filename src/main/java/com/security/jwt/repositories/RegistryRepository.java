package com.security.jwt.repositories;

import com.security.jwt.entities.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Transactional(readOnly=true)
@Repository
public interface RegistryRepository extends JpaRepository<Registry, Integer> {
	
}
