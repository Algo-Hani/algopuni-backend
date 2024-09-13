package algohani.moduleadminapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"algohani.moduleadminapi", "algohani.common"})
public class ModuleAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleAdminApiApplication.class, args);
    }
}
