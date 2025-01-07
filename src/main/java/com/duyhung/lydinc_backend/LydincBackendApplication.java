package com.duyhung.lydinc_backend;

import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class LydincBackendApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(LydincBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            // Add predefined roles with auto-generated IDs
            roleRepository.save(new Role("STUDENT"));
            roleRepository.save(new Role("LECTURER"));

            System.out.println("Predefined roles have been initialized.");
        } else {
            System.out.println("Roles already exist. Skipping initialization.");
        }
    }
}
