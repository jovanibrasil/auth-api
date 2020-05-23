package com.security.web.service;

import com.security.web.domain.User;
import com.security.web.domain.dto.EmailMessageDTO;

public interface IntegrationService {
    void createServiceUser(User user);
    void deleteServiceUser(User user);
    void sendEmail(EmailMessageDTO emailMessage);
}
