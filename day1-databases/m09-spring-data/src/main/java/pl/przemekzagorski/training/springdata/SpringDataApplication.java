package pl.przemekzagorski.training.springdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Główna klasa aplikacji Spring Boot.
 * 
 * @SpringBootApplication łączy:
 * - @Configuration - klasa konfiguracyjna
 * - @EnableAutoConfiguration - automatyczna konfiguracja
 * - @ComponentScan - skanowanie komponentów
 */
@SpringBootApplication
public class SpringDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataApplication.class, args);
    }
}
