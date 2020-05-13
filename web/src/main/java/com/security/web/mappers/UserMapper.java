package com.security.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.security.web.domain.CheckedTokenInfoDTO;
import com.security.web.domain.User;
import com.security.web.domain.dto.UserDTO;
import com.security.web.domain.form.JwtAuthenticationForm;
import com.security.web.domain.form.UpdateUserForm;
import com.security.web.domain.form.UserForm;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    User jwtAuthenticationDtoToUser(JwtAuthenticationForm jwtAuthenticationMapper);
    User updateUserDtoToUser(UpdateUserForm updateUserDTO);

    @Mappings({
            @Mapping(source = "userName", target = "name"),
            @Mapping(source = "profile", target = "role")
    })
    CheckedTokenInfoDTO userToCheckedTokenInfoDto(User user);

	User userFormToUser(UserForm userDto);
	UserDTO userToUserDto(User user);
}
