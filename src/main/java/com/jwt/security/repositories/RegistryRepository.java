package com.jwt.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.security.entities.Registry;

//@Transactional(readOnly=true)
@Repository
public interface RegistryRepository extends JpaRepository<Registry, Integer> {
	
}
