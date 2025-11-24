package com.skhu.oauthgoogleloginpr.global.admin;

import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {

            String adminEmail = "admin@test.com";

            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            User admin = User.builder()
                    .name("admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin1234"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("관리자 계정 생성됨: {} / {}", adminEmail, "admin1234");
        };
    }
}
