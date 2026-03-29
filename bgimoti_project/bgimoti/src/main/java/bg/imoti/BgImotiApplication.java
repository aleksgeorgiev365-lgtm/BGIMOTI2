package bg.imoti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * БГИмоти — Главен клас на Spring Boot приложението
 * Стартирайте с: mvn spring-boot:run
 * След това отворете: http://localhost:8080
 */
@SpringBootApplication
public class BgImotiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BgImotiApplication.class, args);
    }
}
