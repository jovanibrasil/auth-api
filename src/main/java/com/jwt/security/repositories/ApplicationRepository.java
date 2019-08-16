package com.jwt.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.security.entities.Application;

//@Transactional(readOnly=true)
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}
