package com.security.web.domain.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.security.web.domain.User;
import com.security.web.domain.dto.CheckedTokenInfoDTO;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    @Mapping(source = "userName", target = "username")
    User jwtAuthenticationDtoToUser(JwtAuthenticationForm jwtAuthenticationMapper);
    User updateUserDtoToUser(UpdateUserForm updateUserDTO);

    @Mappings({
            @Mapping(source = "username", target = "name"),
            @Mapping(source = "profile", target = "role")
    })
    CheckedTokenInfoDTO userToCheckedTokenInfoDto(User user);
    @Mapping(source = "userName", target = "username")
	User userFormToUser(UserForm userDto);
	UserDTO userToUserDto(User user);
}
