package com.smilecare.config;

import com.smilecare.entity.AppUser;
import com.smilecare.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${DEMO_DENTIST_PASSWORD}")
    private String dentistPassword;

    @Value("${DEMO_PATIENT_PASSWORD}")
    private String patientPassword;

    @Value("${DEMO_RECEPTIONIST_PASSWORD}")
    private String receptionistPassword;

    public DataInitializer(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        createUser(
                "dentist1",
                dentistPassword,
                "DENTIST",
                null,
                1L
        );

        createUser(
                "patient1",
                patientPassword,
                "PATIENT",
                1L,
                null
        );

        createUser(
                "reception1",
                receptionistPassword,
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

        if (userRepository.existsByUsername(username)) {
            return;
        }

        AppUser user = new AppUser();

        user.setUsername(username);

        user.setPasswordHash(
                passwordEncoder.encode(password)
        );

        user.setRole(role);
        user.setPatientId(patientId);
        user.setDentistId(dentistId);
        user.setEnabled(true);

        userRepository.save(user);
    }
}