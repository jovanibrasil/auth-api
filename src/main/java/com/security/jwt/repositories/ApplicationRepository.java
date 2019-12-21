package com.security.jwt.repositories;

import com.security.jwt.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Transactional(readOnly=true)
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}
