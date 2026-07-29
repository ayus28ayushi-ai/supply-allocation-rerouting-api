package com.triage.dera.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data@Builder
public class LoginRequestDto {
    @NotBlank(message = "Field 'email' is mandatory and must be a valid email string (e.g. user@example.com).")
    @Email(message = "Field 'email' must follow standard email format.")
    private String email;

    @NotBlank(message = "Field 'password' is mandatory and must be at least 8 characters long.")
    @Size(min = 8, message = "Password too short. Provide a string with 8+ characters.")
    private String password;

}
