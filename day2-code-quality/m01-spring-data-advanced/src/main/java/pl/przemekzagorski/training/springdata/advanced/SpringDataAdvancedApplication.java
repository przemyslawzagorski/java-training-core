package pl.przemekzagorski.training.springdata.advanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 🏴‍☠️ Spring Data Advanced - Aplikacja główna
 *
 * Ten moduł pokazuje zaawansowane techniki Spring Data JPA:
 * - Flyway - migracje bazy danych
 * - Cache L2 - Hibernate Second Level Cache
 * - Paginacja - Pageable i Page<T>
 * - Projekcje - Spring Data Projections
 * - Batch Processing - wydajny import danych
 * - Read-only mode - optymalizacja zapytań
 *
 * KONTEKST:
 * - Dzień 1, m09: Spring Data podstawy
 * - Dzień 2, m01: Spring Data zaawansowane (TEN MODUŁ)
 *
 * URUCHOMIENIE:
 * mvn spring-boot:run -pl day2-code-quality/m01-spring-data-advanced
 */
@SpringBootApplication
@EnableCaching  // Włącz Cache L2
public class SpringDataAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataAdvancedApplication.class, args);
    }
}

