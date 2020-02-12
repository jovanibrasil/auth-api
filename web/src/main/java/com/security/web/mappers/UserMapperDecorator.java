package com.security.web.mappers;

import com.security.jwt.generator.JwtTokenGenerator;
import com.security.web.domain.*;
import com.security.web.dto.ConfirmUserDTO;
import com.security.web.dto.JwtAuthenticationDTO;
import com.security.web.dto.RegistrationUserDTO;
import com.security.web.dto.UpdateUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

public abstract class UserMapperDecorator implements UserMapper {

    private UserMapper userMapper;
    private JwtTokenGenerator jwtTokenUtil;

    @Override
    public User registrationUserDtoToUser(RegistrationUserDTO registrationUserDTO) {
        User user = userMapper.registrationUserDtoToUser(registrationUserDTO);
        // Get user information from token
        String email = jwtTokenUtil.getEmailFromToken(registrationUserDTO.getToken());
        String applicationName = jwtTokenUtil.getApplicationName(registrationUserDTO.getToken());
        user.setEmail(email);
        Application application = new Application(ApplicationType.valueOf(applicationName));
		user.setRegistries(Arrays.asList(new Registry(application, user)));
        return user;
    }


    @Override
    public User jwtAuthenticationDtoToUser(JwtAuthenticationDTO JwtAuthenticationDTO) {
        User user = userMapper.jwtAuthenticationDtoToUser(JwtAuthenticationDTO);
        user.setRegistries(Arrays.asList(new Registry(new Application(JwtAuthenticationDTO.getApplication()), user)));
        return user;
    }

    @Override
    public User updateUserDtoToUser(UpdateUserDTO updateUserDTO) {
        User user = userMapper.updateUserDtoToUser(updateUserDTO);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        user.setUserName(userName);
        return user;
    }

    @Override
    public User confirmUserDtoToUser(ConfirmUserDTO userDto) {
        User user = userMapper.confirmUserDtoToUser(userDto);
        user.setRegistries(Arrays.asList(new Registry(new Application(userDto.getApplication()), user)));
        return user;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenGenerator jwtTokenUtil) { this.jwtTokenUtil = jwtTokenUtil; }
}
