package com.triage.dera.dto.user;

import com.triage.dera.entity.AuthProvider;
import com.triage.dera.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private AuthProvider provider;
    private Role role;


}
