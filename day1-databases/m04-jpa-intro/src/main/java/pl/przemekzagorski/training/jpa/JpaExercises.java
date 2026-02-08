package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Ship;

import java.util.List;

/**
 * 🏴‍☠️ Ćwiczenia JPA - Pierwsze kroki z ORM
 *
 * ═══════════════════════════════════════════════════════════════════
 * INSTRUKCJA DLA KURSANTA:
 * ═══════════════════════════════════════════════════════════════════
 *
 * 1. Każda metoda exercise_X ma komentarz z TODO opisującym co zrobić
 * 2. Uzupełnij kod w miejscach oznaczonych "// TODO:"
 * 3. Po uzupełnieniu uruchom metodę main() - zobaczysz wyniki
 * 4. Jeśli utkniesz - sprawdź JpaExercisesSolutions.java
 *
 * WAŻNE KONCEPCJE:
 * - EntityManagerFactory - ciężki obiekt, jeden na aplikację
 * - EntityManager - lekki obiekt, jeden na transakcję/operację
 * - persist() - zapisuje nową encję
 * - find() - pobiera encję po ID
 * - JPQL - zapytania na obiektach (nie SQL!)
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class JpaExercises {

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ JPA Exercises - Ćwiczenia");
        System.out.println("═══════════════════════════════\n");

        // Tworzymy EntityManagerFactory - jeden na całą aplikację
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
            System.out.println("   Status: " + (savedId != null && savedId > 0 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 2: Pobranie encji (find)
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 2: Pobierz statek po ID (find)");
            System.out.println("═".repeat(60));

            Ship found = exercise2_FindById(emf, savedId);

            System.out.println("   Znaleziono: " + found);
            System.out.println("   Status: " + (found != null && "Black Pearl".equals(found.getName()) ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

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
            System.out.println("   Status: " + (updated.getCannons() == 64 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

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
            System.out.println("   Status: " + (bigShips.size() == 3 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

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
            System.out.println("   Status: " + (deleted == null ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // ĆWICZENIE 6: Zapytanie z parametrem nazwanym
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📝 ĆWICZENIE 6: Znajdź statki typu Galleon (named parameter)");
            System.out.println("═".repeat(60));

            List<Ship> galleons = exercise6_FindByType(emf, "Galleon");

            System.out.println("   Znaleziono " + galleons.size() + " galeonów:");
            galleons.forEach(s -> System.out.println("   ⛵ " + s.getName()));
            System.out.println("   Status: " + (galleons.size() == 2 ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));

            // ═══════════════════════════════════════════════════════════
            // PODSUMOWANIE
            // ═══════════════════════════════════════════════════════════
            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 PODSUMOWANIE - Wszystkie statki w bazie:");
            System.out.println("═".repeat(60));
            showAllShips(emf);

        } finally {
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 1: Zapisz nowy statek do bazy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Utwórz nowy statek i zapisz go do bazy danych.
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Kroki do wykonania:
     * 1. Uzupełnij tworzenie EntityManager
     * 2. Uzupełnij rozpoczęcie transakcji
     * 3. Uzupełnij tworzenie obiektu Ship
     * 4. Uzupełnij persist()
     * 5. Uzupełnij commit()
     * 6. Zwróć ID statku
     *
     * 💡 Wskazówki:
     * - EntityManager to "okno" do bazy danych
     * - Transakcja wymagana dla operacji modyfikujących (persist, update, remove)
     * - persist() zmienia stan encji z NEW na MANAGED
     * - commit() wykonuje faktyczny INSERT do bazy
     * - Po persist() encja ma już przypisane ID
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     *
     * @return ID zapisanego statku
     */
    private static Long exercise1_PersistShip(EntityManagerFactory emf,
                                               String name, String type, int cannons) {
        // 🔴 BREAKPOINT 1: Ustaw tutaj - PRZED utworzeniem EntityManager
        // 👁️ OBSERWUJ: emf (EntityManagerFactory) - ciężki obiekt, jeden na aplikację
        // 💡 ZADANIE: W Evaluate (Alt+F8) sprawdź: emf.isOpen() - powinno być true

        // TODO 1: Utwórz EntityManager z factory
        EntityManager em = emf.createEntityManager();

        // 🔴 BREAKPOINT 2: Po utworzeniu EM, PRZED begin()
        // 👁️ OBSERWUJ w Variables:
        //    - em.isOpen() = true (EntityManager jest otwarty)
        //    - em.getTransaction().isActive() = false (transakcja jeszcze nie rozpoczęta)
        // 💡 ZADANIE: Sprawdź em.isOpen() w Evaluate

        // TODO 2: Rozpocznij transakcję
        em.getTransaction().begin();

        // 🔴 BREAKPOINT 3: Po begin(), PRZED utworzeniem Ship
        // 👁️ OBSERWUJ: em.getTransaction().isActive() = true (transakcja aktywna)

        // TODO 3: Utwórz obiekt Ship z podanymi parametrami
        Ship ship = new Ship(/* name */ name, /* type */ type, /* cannons */ cannons);

        // 🔴 BREAKPOINT 4: Po utworzeniu Ship, PRZED persist()
        // 👁️ OBSERWUJ w Variables:
        //    - ship.getId() = null (encja w stanie NEW/TRANSIENT - nie ma ID!)
        //    - ship.name, ship.type, ship.cannons - wartości ustawione
        // 💡 ZADANIE: W Evaluate sprawdź: em.contains(ship)
        //    Wynik: false - encja NIE jest jeszcze zarządzana przez EntityManager
        // 💡 KLUCZOWA OBSERWACJA: To jest stan TRANSIENT (NEW)
        //    - Obiekt istnieje w pamięci Java
        //    - NIE istnieje w bazie danych
        //    - EntityManager o nim NIE WIE

        // TODO 4: Zapisz do bazy używając persist()
        em.persist(/* encja */ ship);

        // 🔴 BREAKPOINT 5: PO persist(), PRZED commit()
        // 👁️ OBSERWUJ w Variables:
        //    - ship.getId() - UWAGA! Już ma wartość! (np. 1, 2, 3...)
        // 💡 ZADANIE: Sprawdź em.contains(ship) - teraz zwróci true!
        // 💡 KLUCZOWA OBSERWACJA: persist() zmienia stan z NEW → MANAGED
        //    - Encja jest teraz zarządzana przez EntityManager
        //    - ID zostało automatycznie przypisane (dla IDENTITY od razu)
        //    - Hibernate "śledzi" tę encję (dirty checking)
        // 💡 PYTANIE: Czy INSERT został już wykonany w bazie?
        //    Odpowiedź: NIE! INSERT wykona się dopiero przy commit()
        //    (możesz to sprawdzić w logach SQL - jeszcze nie ma INSERT)

        // TODO 5: Zatwierdź transakcję (wykonuje INSERT)
        em.getTransaction().commit();

        // 🔴 BREAKPOINT 6: PO commit(), PRZED close()
        // 👁️ OBSERWUJ: Teraz INSERT został wykonany w bazie
        // 💡 ZADANIE: Sprawdź logi SQL w konsoli - zobaczysz:
        //    Hibernate: insert into ship (cannons,name,type,id) values (?,?,?,?)
        // 💡 KLUCZOWA OBSERWACJA: commit() wykonuje faktyczny INSERT do bazy

        // TODO 6: Zamknij EntityManager
        em.close();

        // 🔴 BREAKPOINT 7: PO close()
        // 👁️ OBSERWUJ:
        //    - em.isOpen() = false (EntityManager zamknięty)
        //    - ship.getId() - wciąż ma wartość! (obiekt istnieje w pamięci)
        // 💡 ZADANIE: Spróbuj em.contains(ship) - rzuci IllegalStateException!
        //    Dlaczego? Bo EntityManager jest zamknięty
        // 💡 KLUCZOWA OBSERWACJA: Encja jest teraz DETACHED
        //    - Istnieje w pamięci Java (możemy używać ship.getName() itp.)
        //    - Istnieje w bazie danych
        //    - ALE EntityManager o niej nie wie (bo jest zamknięty)
        //    - Zmiany w ship NIE będą automatycznie zapisane do bazy

        // TODO 7: Zwróć ID zapisanego statku
        return ship.getId();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 2: Pobierz statek po ID
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Pobierz statek z bazy po jego ID.
     *
     * 🎯 POZIOM: ŁATWY (szkielet kodu)
     *
     * Kroki do wykonania:
     * 1. Uzupełnij tworzenie EntityManager
     * 2. Uzupełnij find() - podaj klasę i ID
     * 3. Zamknij EntityManager
     * 4. Zwróć znaleziony statek
     *
     * 💡 Wskazówki:
     * - find() to najprostszy sposób pobrania encji po ID
     * - Zwraca null jeśli encja nie istnieje
     * - NIE wymaga transakcji (tylko odczyt)
     * - Składnia: em.find(KlasaEncji.class, wartośćId)
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     *
     * @return znaleziony statek lub null
     */
    private static Ship exercise2_FindById(EntityManagerFactory emf, Long id) {
        // TODO 1: Utwórz EntityManager
        EntityManager em = emf.createEntityManager();

        // TODO 2: Użyj find() do pobrania statku
        Ship ship = em.find(/* klasa */ Ship.class, /* id */ id);

        // TODO 3: Zamknij EntityManager
        em.close();

        // TODO 4: Zwróć znaleziony statek
        return ship;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 3: Zaktualizuj liczbę armat statku
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Zaktualizuj liczbę armat statku.
     *
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Kroki do wykonania:
     * 1. Utwórz EntityManager
     * 2. Rozpocznij transakcję
     * 3. Pobierz statek używając find()
     * 4. Zmień liczbę armat: ship.setCannons(newCannons)
     * 5. Zatwierdź transakcję - Hibernate SAM wykryje zmianę!
     * 6. Zamknij EntityManager
     *
     * 💡 Wskazówki:
     * - To jest DIRTY CHECKING - automatyczne wykrywanie zmian!
     * - NIE musisz wołać żadnej metody "update"!
     * - Encja pobrana w transakcji jest MANAGED
     * - Hibernate śledzi wszystkie zmiany w MANAGED encjach
     * - Przy commit() automatycznie generuje UPDATE dla zmienionych pól
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     */
    private static void exercise3_UpdateCannons(EntityManagerFactory emf,
                                                 Long shipId, int newCannons) {
        // TODO 1: Utwórz EntityManager
        // EntityManager em = emf.createEntityManager();

        // TODO 2: Rozpocznij transakcję
        // em.getTransaction().begin();

        // 🔴 BREAKPOINT 1: Po begin(), PRZED find()
        // 👁️ OBSERWUJ: Transakcja aktywna, zaraz pobierzemy encję

        // TODO 3: Pobierz statek używając find()
        // Ship ship = em.find(Ship.class, shipId);

        // 🔴 BREAKPOINT 2: Po find(), PRZED setCannons()
        // 👁️ OBSERWUJ w Variables:
        //    - ship - rozwiń i zobacz wszystkie pola
        //    - ship.cannons - obecna wartość (np. 32)
        //    - ship.id - ma wartość (encja z bazy)
        // 💡 ZADANIE: Sprawdź em.contains(ship) - zwróci true (encja MANAGED)
        // 💡 KLUCZOWA OBSERWACJA: Hibernate utworzył "snapshot" encji!
        //    - Zapamiętał wszystkie wartości pól w momencie pobrania
        //    - Ten snapshot będzie użyty do wykrycia zmian przy commit()
        //    - To jest podstawa mechanizmu DIRTY CHECKING

        // TODO 4: Zmień liczbę armat (setter)
        // ship.setCannons(newCannons);

        // 🔴 BREAKPOINT 3: PO setCannons(), PRZED commit()
        // 👁️ OBSERWUJ w Variables:
        //    - ship.cannons - nowa wartość (np. 64)
        // 💡 PYTANIE: Czy wywołaliśmy jakąś metodę "update()" lub "save()"? NIE!
        // 💡 PYTANIE: Skąd Hibernate wie że coś się zmieniło?
        //    Odpowiedź: Przy commit() porówna obecny stan ze "snapshot"!
        // 💡 KLUCZOWA OBSERWACJA: To jest "magia" JPA - DIRTY CHECKING
        //    - Encja MANAGED jest automatycznie śledzona
        //    - Hibernate pamięta jej początkowy stan (snapshot)
        //    - Przy commit() wykryje różnice i wygeneruje UPDATE
        //    - NIE musisz wołać żadnej metody update()!

        // TODO 5: Zatwierdź transakcję - UPDATE wykona się automatycznie!
        // em.getTransaction().commit();

        // 🔴 BREAKPOINT 4: PO commit() - ustaw i naciśnij F8 (Step Over)
        // 👁️ OBSERWUJ: Logi SQL w konsoli - zobaczysz:
        //    Hibernate: update ship set cannons=?, name=?, type=? where id=?
        // 💡 KLUCZOWA OBSERWACJA: To jest DIRTY CHECKING w akcji!
        //    1. Hibernate zapamiętał stan przy find() (snapshot)
        //    2. Przy commit() porównał obecny stan ze snapshot
        //    3. Wykrył zmianę w polu cannons
        //    4. Automatycznie wygenerował UPDATE tylko dla zmienionego wiersza!
        // 💡 EKSPERYMENT: Zmień 2 pola (np. cannons i name) - UPDATE zaktualizuje oba!
        // 💡 EKSPERYMENT: Nie zmieniaj nic - UPDATE się NIE wykona (optymalizacja)!

        // TODO 6: Zamknij EntityManager
        // em.close();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 4: Znajdź statki z minimalną liczbą armat (JPQL)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Znajdź wszystkie statki z liczbą armat >= minCannons.
     *
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Kroki do wykonania:
     * 1. Utwórz EntityManager
     * 2. Napisz JPQL z parametrem nazwanym
     * 3. Utwórz TypedQuery
     * 4. Ustaw parametr
     * 5. Wykonaj zapytanie
     * 6. Zamknij EntityManager
     * 7. Zwróć wynik
     *
     * 💡 Wskazówki:
     * - JPQL = Java Persistence Query Language
     * - Używamy NAZW KLAS i PÓL Java, nie nazw tabel i kolumn SQL!
     * - "Ship" to nazwa klasy, "s.cannons" to pole Java
     * - Parametry nazwane: :nazwaParametru
     * - TypedQuery<Ship> zapewnia type-safety
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     */
    private static List<Ship> exercise4_FindShipsWithMinCannons(EntityManagerFactory emf,
                                                                 int minCannons) {
        // TODO 1: Utwórz EntityManager
        // EntityManager em = emf.createEntityManager();

        // TODO 2: Napisz JPQL - SELECT s FROM Ship s WHERE s.cannons >= :minCannons
        // String jpql = "";

        // TODO 3: Utwórz TypedQuery i ustaw parametr
        // List<Ship> ships = em.createQuery(jpql, Ship.class)
        //     .setParameter("minCannons", minCannons)
        //     .getResultList();

        // TODO 4: Zamknij EntityManager
        // em.close();

        // TODO 5: Zwróć wynik
        // return ships;

        return List.of(); // <-- ZMIEŃ - odkomentuj powyższy kod
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 5: Usuń statek z bazy
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Usuń statek o podanym ID z bazy danych.
     *
     * 🎯 POZIOM: ŚREDNI (struktura + TODO)
     *
     * Kroki do wykonania:
     * 1. Utwórz EntityManager
     * 2. Rozpocznij transakcję
     * 3. Pobierz statek używając find()
     * 4. Jeśli istnieje, usuń: em.remove(ship)
     * 5. Zatwierdź transakcję
     * 6. Zamknij EntityManager
     *
     * 💡 Wskazówki:
     * - remove() wymaga encji MANAGED (pobranej w tej samej transakcji)
     * - Nie możesz usunąć encji DETACHED (np. pobranej wcześniej)
     * - Sprawdź czy statek istnieje (ship != null) przed remove()
     * - commit() wykona DELETE w bazie
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     */
    private static void exercise5_RemoveShip(EntityManagerFactory emf, Long shipId) {
        // TODO 1: Utwórz EntityManager
        // EntityManager em = emf.createEntityManager();

        // TODO 2: Rozpocznij transakcję
        // em.getTransaction().begin();

        // TODO 3: Pobierz statek używając find()
        // Ship ship = em.find(Ship.class, shipId);

        // TODO 4: Jeśli istnieje, usuń
        // if (ship != null) {
        //     em.remove(ship);
        // }

        // TODO 5: Zatwierdź transakcję (wykonuje DELETE)
        // em.getTransaction().commit();

        // TODO 6: Zamknij EntityManager
        // em.close();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 6: Znajdź statki po typie (named parameter)
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Znajdź wszystkie statki określonego typu.
     *
     * 🎯 POZIOM: TRUDNY (tylko wskazówki)
     *
     * Wymagania:
     * 1. Użyj JPQL do wyszukania statków po typie
     * 2. Użyj parametru nazwanego (named parameter)
     * 3. Zwróć listę statków
     *
     * 💡 Wskazówki:
     * - JPQL: "SELECT s FROM Ship s WHERE s.type = :nazwaParametru"
     * - Parametr nazwany zaczyna się od dwukropka :
     * - createQuery() przyjmuje 2 argumenty: JPQL i klasę wyniku
     * - setParameter() ustawia wartość parametru nazwanego
     * - getResultList() zwraca List<Ship>
     * - Nie zapomnij zamknąć EntityManager!
     *
     * 🔍 Struktura rozwiązania:
     * - Utwórz EntityManager
     * - Napisz JPQL z parametrem :shipType
     * - Utwórz TypedQuery<Ship>
     * - Ustaw parametr "shipType" na wartość type
     * - Wykonaj getResultList()
     * - Zamknij EntityManager
     * - Zwróć wynik
     *
     * 🆘 Jeśli utkniesz, sprawdź JpaExercisesSolutions.java
     */
    private static List<Ship> exercise6_FindByType(EntityManagerFactory emf, String type) {
        // TODO: Zaimplementuj wyszukiwanie po typie używając JPQL
        // Struktura:
        // - EntityManager em = ...
        // - List<Ship> ships = em.createQuery("...", Ship.class)
        //       .setParameter("...", ...)
        //       .getResultList();
        // - em.close();
        // - return ships;

        return List.of(); // <-- ZMIEŃ - zaimplementuj logikę powyżej
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE (NIE MODYFIKUJ)
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
