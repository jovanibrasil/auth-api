package com.security.web.mappers;

import com.security.web.domain.CheckedTokenInfoDTO;
import com.security.web.domain.User;
import com.security.web.dto.ConfirmUserDTO;
import com.security.web.dto.JwtAuthenticationDTO;
import com.security.web.dto.RegistrationUserDTO;
import com.security.web.dto.UpdateUserDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    User registrationUserDtoToUser(RegistrationUserDTO registrationUserDTO);
    User jwtAuthenticationDtoToUser(JwtAuthenticationDTO jwtAuthenticationMapper);
    User updateUserDtoToUser(UpdateUserDTO updateUserDTO);

    @Mappings({
            @Mapping(source = "userName", target = "name"),
            @Mapping(source = "profile", target = "role")
    })
    CheckedTokenInfoDTO userToCheckedTokenInfoDto(User user);

    User confirmUserDtoToUser(ConfirmUserDTO userDto);
}
