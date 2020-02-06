package com.security.web.repositories;

import com.security.web.domain.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Transactional(readOnly=true)
@Repository
public interface RegistryRepository extends JpaRepository<Registry, Integer> {
	
}
