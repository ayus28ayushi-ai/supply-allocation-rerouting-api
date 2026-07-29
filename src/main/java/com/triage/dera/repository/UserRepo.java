package com.triage.dera.repository;

import com.triage.dera.entity.Users;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Long > {


    Optional<Users> findByEmail(@NonNull String email);

    Boolean existsByEmail(String adminEmail);
}
