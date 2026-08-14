package com.triage.dera.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}
