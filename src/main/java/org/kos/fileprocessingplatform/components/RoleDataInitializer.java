package org.kos.fileprocessingplatform.components;

import lombok.RequiredArgsConstructor;
import org.kos.fileprocessingplatform.models.ERole;
import org.kos.fileprocessingplatform.models.RoleEntity;
import org.kos.fileprocessingplatform.models.UserEntity;
import org.kos.fileprocessingplatform.repositories.RoleEntityRepo;
import org.kos.fileprocessingplatform.repositories.UserEntityRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RoleEntityRepo roleEntityRepo;
    private final UserEntityRepo userEntityRepo;

    @Override
    public void run(String... args) {
        createRoleIfNotExists(ERole.ROLE_USER);
        createRoleIfNotExists(ERole.ROLE_ADMIN);
        createUserIfNotExists();
    }

    private void createRoleIfNotExists(ERole roleName) {
        roleEntityRepo.findByName(roleName)
                .orElseGet(() -> roleEntityRepo.save(
                        RoleEntity.builder()
                                .name(roleName)
                                .build()
                ));
    }

    private void createUserIfNotExists() {
        if (userEntityRepo.findByUsername("kosfi").isPresent()) {
            return;
        }

        RoleEntity adminRole = roleEntityRepo.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        userEntityRepo.save(
                UserEntity.builder()
                        .username("kosfi")
                        .email("kwstas.kosfi@gmail.com")
                        .password(passwordEncoder.encode("12345"))
                        .confirmPassword(passwordEncoder.encode("12345"))
                        .creationDate(Instant.now())
                        .roles(Set.of(adminRole))
                        .build()
        );
    }
}