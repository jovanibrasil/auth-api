package com.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.jwt.entities.Application;

//@Transactional(readOnly=true)
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}
