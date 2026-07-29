package com.triage.dera.mappers;

import com.triage.dera.dto.user.RegisterRequestDto;
import com.triage.dera.dto.user.UserResponseDto;
import com.triage.dera.entity.AuthProvider;
import com.triage.dera.entity.Role;
import com.triage.dera.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMappers {

    public Users mapRegisterRequestDtoToEntity(RegisterRequestDto registerRequestDto, String encodedPassword){
      if(registerRequestDto == null){
          return null;
      }
       return Users.builder()
                .username(registerRequestDto.getUsername())
                .email(registerRequestDto.getEmail())
                .password(encodedPassword)
                .provider(AuthProvider.LOCAL)
                .role(Role.ROLE_USER)
                .build();
    }

    public UserResponseDto mapEntityToUserResponseDto(Users user){
        if (user == null){
            return null;
        }
        return UserResponseDto.builder()
                 .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();
    }
}
