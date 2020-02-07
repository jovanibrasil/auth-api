package com.security.web.services;

import com.security.web.domain.User;
import com.security.web.dto.EmailMessage;

public interface IntegrationService {
    void createServiceUser(User user);
    void deleteServiceUser(User user);
    void sendEmail(EmailMessage emailMessage);
}
