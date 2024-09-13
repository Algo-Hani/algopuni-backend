package algohani.moduleuserapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"algohani.moduleuserapi", "algohani.common"})
public class ModuleUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleUserApiApplication.class, args);
    }

}
