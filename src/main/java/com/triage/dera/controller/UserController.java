package com.triage.dera.controller;

import com.triage.dera.dto.user.AuthResponseDto;
import com.triage.dera.dto.user.LoginRequestDto;
import com.triage.dera.dto.user.RegisterRequestDto;
import com.triage.dera.dto.user.UserResponseDto;
import com.triage.dera.service.AppUserDetailsService;
import com.triage.dera.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dera")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(registerRequestDto));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.verifyUser(loginRequestDto));
    }

    //protected endpoint from where a verified admin can add a new admin
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> registerNewAdmin (@Valid @RequestBody RegisterRequestDto registerRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerNewAdmin(registerRequestDto));
    }

}
