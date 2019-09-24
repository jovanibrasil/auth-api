package com.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.jwt.entities.Registry;

//@Transactional(readOnly=true)
@Repository
public interface RegistryRepository extends JpaRepository<Registry, Integer> {
	
}
