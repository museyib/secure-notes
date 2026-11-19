package llc.devlab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SecureNotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureNotesApplication.class, args);
    }

}
