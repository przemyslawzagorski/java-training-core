package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.util.List;

/**
 * 🏴‍☠️ JPA Exercises - ROZWIĄZANIA
 *
 * ═══════════════════════════════════════════════════════════════════
 * Ten plik zawiera pełne rozwiązania ćwiczeń z JpaExercises.java.
 *
 * UWAGA: Najpierw spróbuj rozwiązać ćwiczenia samodzielnie!
 * Zaglądaj tu tylko gdy naprawdę utkniesz.
 * ═══════════════════════════════════════════════════════════════════
 */
public class JpaExercisesSolutions {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JPA Exercises - ROZWIĄZANIA");
        System.out.println("═══════════════════════════════════\n");

        // Tworzymy EntityManagerFactory - jeden na całą aplikację
        // Jest to operacja "ciężka" - ładowanie metadanych, tworzenie connection pool itp.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pirates-pu");

        try {
            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 1: Zapisanie encji (persist)
            // ═══════════════════════════════════════════════════════════
            System.out.println("═".repeat(60));
            System.out.println("📝 ĆWICZENIE 1: Zapisz statek do bazy (persist)");
            System.out.println("═".repeat(60));

            Long savedId = exercise1_PersistShip(emf, "Black Pearl", "Galleon", 32);

            System.out.println("   Zapisano statek z ID: " + savedId);
            System.out.println("   Status: " + (savedId != null && savedId > 0 ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 2: Pobranie encji (find)
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 2: Pobierz statek po ID (find)");
            System.out.println("═".repeat(60));

            Ship found = exercise2_FindById(emf, savedId);

            System.out.println("   Znaleziono: " + found);
            System.out.println("   Status: " + (found != null && "Black Pearl".equals(found.getName()) ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 3: Aktualizacja encji
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 3: Zaktualizuj liczbę armat (auto-update)");
            System.out.println("═".repeat(60));

            exercise3_UpdateCannons(emf, savedId, 64);

            // Weryfikacja
            Ship updated = exercise2_FindById(emf, savedId);
            System.out.println("   Po aktualizacji: " + updated.getCannons() + " armat");
            System.out.println("   Status: " + (updated.getCannons() == 64 ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 4: Zapytanie JPQL
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 4: Znajdź statki z > 30 armatami (JPQL)");
            System.out.println("═".repeat(60));

            // Najpierw dodajmy więcej statków
            exercise1_PersistShip(emf, "Flying Dutchman", "Galleon", 46);
            exercise1_PersistShip(emf, "Interceptor", "Sloop", 16);
            exercise1_PersistShip(emf, "Queen Anne's Revenge", "Frigate", 40);

            List<Ship> bigShips = exercise4_FindShipsWithMinCannons(emf, 30);

            System.out.println("   Znaleziono " + bigShips.size() + " statków:");
            bigShips.forEach(s -> System.out.println("   ⚓ " + s.getName() + " (" + s.getCannons() + " armat)"));
            System.out.println("   Status: " + (bigShips.size() == 3 ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 5: Usunięcie encji (remove)
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 5: Usuń statek Interceptor (remove)");
            System.out.println("═".repeat(60));

            // Znajdźmy ID Interceptora
            Long interceptorId = findShipIdByName(emf, "Interceptor");
            System.out.println("   ID Interceptora: " + interceptorId);

            exercise5_RemoveShip(emf, interceptorId);

            // Weryfikacja
            Ship deleted = exercise2_FindById(emf, interceptorId);
            System.out.println("   Po usunięciu: " + (deleted == null ? "nie istnieje" : "wciąż istnieje!"));
            System.out.println("   Status: " + (deleted == null ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 6: Zapytanie z parametrem nazwanym
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 6: Znajdź statki typu Galleon (named parameter)");
            System.out.println("═".repeat(60));

            List<Ship> galleons = exercise6_FindByType(emf, "Galleon");

            System.out.println("   Znaleziono " + galleons.size() + " galeonów:");
            galleons.forEach(s -> System.out.println("   ⛵ " + s.getName()));
            System.out.println("   Status: " + (galleons.size() == 2 ? "✅ POPRAWNIE!" : "❌ Błąd"));

            // ═══════════════════════════════════════════════════════════
            // PODSUMOWANIE
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 PODSUMOWANIE - Wszystkie statki w bazie:");
            System.out.println("═".repeat(60));
            showAllShips(emf);

        } finally {
            // Zawsze zamykamy EMF na końcu aplikacji
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 1: Zapisz nowy statek do bazy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Zapisywanie nowej encji.
     *
     * KLUCZOWE KONCEPCJE:
     * 1. EntityManager - "okno" do bazy danych, lekki obiekt
     * 2. Transakcja - wymagana dla operacji modyfikujących (persist, update, remove)
     * 3. persist() - zmienia stan encji z NEW na MANAGED i planuje INSERT
     * 4. commit() - wykonuje faktyczne operacje SQL i kończy transakcję
     *
     * CYKL ŻYCIA ENCJI:
     * NEW → persist() → MANAGED → commit() → DETACHED (po zamknięciu EM)
     *
     * Po persist() encja ma już przypisane ID (dla IDENTITY) lub zostanie
     * przypisane podczas commit() (dla SEQUENCE/TABLE).
     */
    private static Long exercise1_PersistShip(EntityManagerFactory emf,
                                               String name, String type, int cannons) {
        // 1. Tworzymy EntityManager - lekki obiekt, jeden na operację/transakcję
        EntityManager em = emf.createEntityManager();

        // 2. Rozpoczynamy transakcję - wymagana dla operacji modyfikujących
        em.getTransaction().begin();

        // 3. Tworzymy obiekt Ship - na razie jest w stanie NEW (nie związany z bazą)
        Ship ship = new Ship(name, type, cannons);

        // 4. persist() - zmienia stan na MANAGED
        //    - Encja jest teraz śledzona przez Persistence Context
        //    - ID zostanie przypisane (dla IDENTITY od razu, dla SEQUENCE przy commit)
        //    - INSERT zostanie wykonany przy commit()
        em.persist(ship);

        // 5. commit() - faktycznie wykonuje INSERT
        //    - Kończy transakcję
        //    - Encja przechodzi do stanu DETACHED (po zamknięciu EM)
        em.getTransaction().commit();

        // 6. Zamykamy EntityManager - zwalniamy zasoby
        //    - Encja ship jest teraz DETACHED (odłączona od kontekstu)
        em.close();

        // 7. Zwracamy ID - zostało przypisane po persist()/commit()
        return ship.getId();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 2: Pobierz statek po ID
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Pobieranie encji po kluczu głównym.
     *
     * KLUCZOWE KONCEPCJE:
     * 1. find() - pobiera encję po ID, zwraca null jeśli nie istnieje
     * 2. Odczyt NIE wymaga transakcji (ale może być w transakcji)
     * 3. Pobrana encja jest w stanie MANAGED (śledzona przez kontekst)
     *
     * find() vs getReference():
     * - find() - wykonuje SELECT od razu, zwraca encję lub null
     * - getReference() - zwraca proxy, SELECT dopiero przy dostępie do pól
     *
     * Dla prostych przypadków używaj find(), getReference() dla optymalizacji
     * gdy chcesz tylko ustawić relację.
     */
    private static Ship exercise2_FindById(EntityManagerFactory emf, Long id) {
        // 1. Tworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        // 2. find() - wykonuje SELECT * FROM ships WHERE id = ?
        //    - Zwraca encję w stanie MANAGED lub null
        //    - NIE wymaga transakcji (ale może być w transakcji)
        Ship ship = em.find(Ship.class, id);

        // 3. Zamykamy EntityManager
        //    - ship przechodzi do stanu DETACHED
        //    - Możemy go nadal używać, ale zmiany nie będą śledzone
        em.close();

        // 4. Zwracamy znalezioną encję (lub null)
        return ship;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 3: Zaktualizuj liczbę armat statku
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Automatyczna aktualizacja (dirty checking).
     *
     * KLUCZOWE KONCEPCJE:
     * 1. Encja MANAGED jest automatycznie śledzona
     * 2. Hibernate wykrywa zmiany przy commit() (dirty checking)
     * 3. NIE ma metody "update()" - zmiany są automatyczne!
     *
     * JAK TO DZIAŁA:
     * 1. find() pobiera encję → stan MANAGED
     * 2. Zmieniamy pole przez setter
     * 3. commit() → Hibernate porównuje stan obecny z początkowym
     * 4. Jeśli są różnice → generuje UPDATE
     *
     * To jest jedna z najważniejszych cech JPA - transparentna persystencja!
     */
    private static void exercise3_UpdateCannons(EntityManagerFactory emf,
                                                 Long shipId, int newCannons) {
        // 1. Tworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        // 2. Rozpoczynamy transakcję - wymagana dla modyfikacji
        em.getTransaction().begin();

        // 3. Pobieramy encję - jest teraz MANAGED
        //    Hibernate zapamiętuje jej początkowy stan (snapshot)
        Ship ship = em.find(Ship.class, shipId);

        // 4. Zmieniamy pole - Hibernate tego jeszcze "nie widzi"
        //    Ale śledzi encję i przy commit() porówna stany
        ship.setCannons(newCannons);

        // 5. commit() - tutaj dzieje się magia!
        //    - Hibernate wykonuje "dirty checking"
        //    - Porównuje obecny stan z zapamiętanym snapshot
        //    - Wykrywa zmianę w polu cannons
        //    - Generuje: UPDATE ships SET cannons = ? WHERE id = ?
        em.getTransaction().commit();

        // 6. Zamykamy EntityManager
        em.close();

        // UWAGA: Gdybyśmy nie zmienili żadnego pola, UPDATE by się NIE wykonał!
        // To optymalizacja - Hibernate nie robi zbędnych operacji.
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 4: Znajdź statki z minimalną liczbą armat (JPQL)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Zapytanie JPQL z parametrem.
     *
     * KLUCZOWE KONCEPCJE:
     * 1. JPQL - Java Persistence Query Language
     * 2. Operuje na OBIEKTACH, nie na tabelach!
     * 3. Używamy nazw klas i pól Java, nie SQL
     *
     * JPQL vs SQL:
     * - SQL:  SELECT * FROM ships WHERE cannons >= 30
     * - JPQL: SELECT s FROM Ship s WHERE s.cannons >= :minCannons
     *
     * PARAMETRY:
     * - Nazwane: :paramName - czytelniejsze, zalecane
     * - Pozycyjne: ?1, ?2 - krótsze, ale mniej czytelne
     *
     * TypedQuery<T> vs Query:
     * - TypedQuery - bezpieczny typowo, zwraca List<T>
     * - Query - zwraca List<?>, wymaga rzutowania
     */
    private static List<Ship> exercise4_FindShipsWithMinCannons(EntityManagerFactory emf,
                                                                 int minCannons) {
        // 1. Tworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        // 2. Piszemy JPQL - na obiektach, nie SQL!
        //    - "Ship" to nazwa klasy Java (nie tabeli!)
        //    - "s.cannons" to pole Java (nie kolumna!)
        //    - ":minCannons" to nazwany parametr
        String jpql = "SELECT s FROM Ship s WHERE s.cannons >= :minCannons";

        // 3. Tworzymy TypedQuery - bezpieczne typowo
        //    - Dzięki Ship.class wiemy, że wynikiem będzie List<Ship>
        List<Ship> ships = em.createQuery(jpql, Ship.class)
                // 4. Ustawiamy parametr - NIGDY nie konkatenuj stringów!
                //    To chroni przed SQL injection
                .setParameter("minCannons", minCannons)
                // 5. Wykonujemy zapytanie
                .getResultList();

        // 6. Zamykamy EntityManager
        em.close();

        // 7. Zwracamy wynik - encje są DETACHED
        return ships;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 5: Usuń statek z bazy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Usuwanie encji.
     *
     * KLUCZOWE KONCEPCJE:
     * 1. remove() działa TYLKO na encjach MANAGED!
     * 2. Najpierw musimy pobrać encję (find), potem usunąć
     * 3. Encja przechodzi do stanu REMOVED
     * 4. DELETE wykonuje się przy commit()
     *
     * CYKL ŻYCIA:
     * DETACHED → find() → MANAGED → remove() → REMOVED → commit() → usunięta
     *
     * CZĘSTY BŁĄD:
     * Ship ship = new Ship(); ship.setId(1L);
     * em.remove(ship); // BŁĄD! ship jest NEW, nie MANAGED!
     */
    private static void exercise5_RemoveShip(EntityManagerFactory emf, Long shipId) {
        // 1. Tworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        // 2. Rozpoczynamy transakcję
        em.getTransaction().begin();

        // 3. NAJPIERW pobieramy encję - musi być MANAGED
        //    Nie możemy usunąć encji DETACHED lub NEW!
        Ship ship = em.find(Ship.class, shipId);

        // 4. Sprawdzamy czy istnieje i usuwamy
        if (ship != null) {
            // remove() zmienia stan na REMOVED
            // DELETE wykona się przy commit()
            em.remove(ship);
        }

        // 5. commit() - wykonuje DELETE FROM ships WHERE id = ?
        em.getTransaction().commit();

        // 6. Zamykamy EntityManager
        em.close();

        // ALTERNATYWA - bulk delete (bez ładowania encji):
        // em.createQuery("DELETE FROM Ship s WHERE s.id = :id")
        //   .setParameter("id", shipId)
        //   .executeUpdate();
        // Ale to omija cache i lifecycle callbacks!
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 6: Znajdź statki po typie (named parameter)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Proste zapytanie JPQL z parametrem tekstowym.
     *
     * KLUCZOWE KONCEPCJE:
     * 1. Parametry chronią przed SQL injection
     * 2. Hibernate automatycznie escapuje wartości
     * 3. Nigdy nie używaj konkatenacji stringów!
     *
     * ŹLE:
     * "SELECT s FROM Ship s WHERE s.type = '" + type + "'"  // SQL Injection!
     *
     * DOBRZE:
     * "SELECT s FROM Ship s WHERE s.type = :shipType"
     * .setParameter("shipType", type)
     */
    private static List<Ship> exercise6_FindByType(EntityManagerFactory emf, String type) {
        // 1. Tworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        // 2. Wykonujemy zapytanie JPQL z parametrem
        List<Ship> ships = em.createQuery(
                        "SELECT s FROM Ship s WHERE s.type = :shipType", Ship.class)
                .setParameter("shipType", type)
                .getResultList();

        // 3. Zamykamy EntityManager
        em.close();

        // 4. Zwracamy wynik
        return ships;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE
    // ═══════════════════════════════════════════════════════════════════════

    private static Long findShipIdByName(EntityManagerFactory emf, String name) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT s.id FROM Ship s WHERE s.name = :name", Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    private static void showAllShips(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        List<Ship> ships = em.createQuery("SELECT s FROM Ship s ORDER BY s.id", Ship.class)
                .getResultList();

        if (ships.isEmpty()) {
            System.out.println("   (brak statków w bazie)");
        } else {
            ships.forEach(s -> System.out.printf("   [%d] %s (%s, %d armat)%n",
                    s.getId(), s.getName(), s.getType(), s.getCannons()));
        }
        em.close();
    }
}
