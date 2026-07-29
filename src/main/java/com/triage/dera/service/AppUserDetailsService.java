package com.triage.dera.service;

import com.triage.dera.entity.UserPrincipal;
import com.triage.dera.entity.Users;
import com.triage.dera.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//just a class to use the interface UserDetailsService
public class AppUserDetailsService implements UserDetailsService {

    public final UserRepo userRepo;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
       return userRepo.findByEmail(email)
               .map(UserPrincipal::new)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with the email: "+ email));
     }
}
