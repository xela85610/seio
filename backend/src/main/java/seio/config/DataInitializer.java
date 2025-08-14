package seio.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import seio.model.enumeration.Role;
import seio.service.UtilisateurService;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner init(UtilisateurService service) {
        return args -> {
            try {
                service.createUser("admin", "admin123", Role.ADMIN);
            } catch (IllegalArgumentException ignored) {}
            try {
                service.createUser("user", "user123", Role.USER);
            } catch (IllegalArgumentException ignored) {}
        };
    }
}
