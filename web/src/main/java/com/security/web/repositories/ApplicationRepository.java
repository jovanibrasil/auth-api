package com.security.web.repositories;

import com.security.web.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Transactional(readOnly=true)
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}