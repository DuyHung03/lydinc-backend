package com.duyhung.lydinc_backend;

import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.repository.RoleRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashSet;

@EnableWebMvc
@SpringBootApplication
@RequiredArgsConstructor
public class LydincBackendApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(LydincBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeAdminAccount();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("STUDENT"));
            roleRepository.save(new Role("LECTURER"));
            roleRepository.save(new Role("ADMIN"));
            System.out.println("Predefined roles have been initialized.");
        } else {
            System.out.println("Roles already exist. Skipping initialization.");
        }
    }

    private void initializeAdminAccount() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleId(3);

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("vkksieunhan2003@gmail.com");
            admin.setName("Admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(new HashSet<>());
            admin.setIsAccountGranted(1);
            admin.setIsPasswordChanged(1);
            admin.getRoles().add(adminRole);

            userRepository.save(admin);
            System.out.println("Admin account has been initialized.");
        } else {
            System.out.println("Users already exist. Skipping admin initialization.");
        }
    }
}
