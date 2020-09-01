package com.security.web.domain.mappers;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.security.web.domain.Application;
import com.security.web.domain.Registry;
import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;

public abstract class UserMapperDecorator implements UserMapper {

    private UserMapper userMapper;

    @Override
    public User jwtAuthenticationDtoToUser(JwtAuthenticationForm jwtAuthenticationDTO) {
        User user = userMapper.jwtAuthenticationDtoToUser(jwtAuthenticationDTO);
        user.setRegistries(Arrays.asList(new Registry(new Application(jwtAuthenticationDTO.getApplication()), user)));
        return user;
    }
    
    @Override
    public User userFormToUser(UserForm userForm) {
        User user = userMapper.userFormToUser(userForm);
        user.setRegistries(Arrays.asList(new Registry(new Application(userForm.getApplication()), user)));
        return user;
    }

    @Override
    public User updateUserDtoToUser(UpdateUserForm updateUserDTO) {
        User user = userMapper.updateUserDtoToUser(updateUserDTO);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        user.setUsername(userName);
        return user;
    }

    @Override
    public UserDTO userToUserDto(User user) {
    	UserDTO userDto = userMapper.userToUserDto(user);
    	userDto.setApplication(user.getRegistries().get(0).getApplication().getType());
    	return userDto;
    }
    
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

}
