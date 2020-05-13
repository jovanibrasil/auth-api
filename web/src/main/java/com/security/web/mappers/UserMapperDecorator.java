package com.security.web.mappers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.security.web.domain.Application;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.form.UpdateUserForm;

public abstract class UserMapperDecorator implements UserMapper {

    private UserMapper userMapper;

    @Override
    public User jwtAuthenticationDtoToUser(JwtAuthenticationForm JwtAuthenticationDTO) {
        User user = userMapper.jwtAuthenticationDtoToUser(JwtAuthenticationDTO);
        user.setRegistries(Arrays.asList(new Registry(new Application(JwtAuthenticationDTO.getApplication()), user)));
        return user;
    }

    @Override
    public User updateUserDtoToUser(UpdateUserForm updateUserDTO) {
        User user = userMapper.updateUserDtoToUser(updateUserDTO);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        user.setUserName(userName);
        return user;
    }

    @Override
    public UserDTO userToUserDto(User user) {
    	UserDTO userDto = userMapper.userToUserDto(user);
    	userDto.setApplication(user.getRegistries().get(0).getApplication().getApplication());
    	return userDto;
    }
    
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

}
