package com.smilecare.config;

import com.smilecare.entity.AppUser;
import com.smilecare.repository.AppUserRepository;

import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;

@Component
public class DataInitializer
        implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Override
    public void run(String... args) {

        createUser(
                "dentist1",
                "Dentist123!",
                "DENTIST",
                null,
                1L
        );

        createUser(
                "patient1",
                "Patient123!",
                "PATIENT",
                1L,
                null
        );

        createUser(
                "reception1",
                "Reception123!",
                "RECEPTIONIST",
                null,
                null
        );
    }

    private void createUser(
            String username,
            String password,
            String role,
            Long patientId,
            Long dentistId) {

        if (userRepository
                .existsByUsername(username)) {

            return;
        }

        AppUser user =
                new AppUser();

        user.setUsername(username);

        user.setPasswordHash(
                passwordEncoder
                        .encode(password)
        );

        user.setRole(role);
        user.setPatientId(patientId);
        user.setDentistId(dentistId);
        user.setEnabled(true);

        userRepository.save(user);
    }
}