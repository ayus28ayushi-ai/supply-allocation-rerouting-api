package com.triage.dera.service;

import com.triage.dera.dto.user.AuthResponseDto;
import com.triage.dera.dto.user.LoginRequestDto;
import com.triage.dera.dto.user.RegisterRequestDto;
import com.triage.dera.dto.user.UserResponseDto;
import com.triage.dera.entity.AuthProvider;
import com.triage.dera.entity.Role;
import com.triage.dera.entity.UserPrincipal;
import com.triage.dera.entity.Users;
import com.triage.dera.mappers.UserMappers;
import com.triage.dera.repository.UserRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final UserMappers mappers;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public UserResponseDto register(RegisterRequestDto registerRequestDto) {
        String encodedPass = passwordEncoder.encode(registerRequestDto.getPassword());
        Users user = mappers.mapRegisterRequestDtoToEntity(registerRequestDto, encodedPass);
        Users savedUser = userRepo.save(user);
        return mappers.mapEntityToUserResponseDto(savedUser);



    }

    public AuthResponseDto verifyUser(@Valid LoginRequestDto loginRequestDto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        assert principal != null;
        Users user = principal.getUser();

        String token = jwtService.generateToken(principal);
        UserResponseDto response = mappers.mapEntityToUserResponseDto(user);

        return new AuthResponseDto(token, response);
    }

    public UserResponseDto registerNewAdmin(@Valid RegisterRequestDto registerRequestDto) {
        Users newAdmin = mappers
                .mapRegisterRequestDtoToEntity(
                        registerRequestDto,
                        passwordEncoder.encode(registerRequestDto.getPassword())
                );
        newAdmin.setRole(Role.ROLE_ADMIN);
        Users savedAdmin = userRepo.save(newAdmin);
        return mappers.mapEntityToUserResponseDto(savedAdmin);
    }

    //find an existing user or create a new one when log in using GOOGLE or GITHUB
    @Transactional
    public UserPrincipal processOAuthUser(String email, String username, AuthProvider provider) {
        Users user = userRepo.findByEmail(email).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setProvider(provider);
            newUser.setPassword(" ");
            newUser.setRole(Role.ROLE_USER);
            return userRepo.save(newUser);
        });

        return new UserPrincipal(user);
    }
}
















