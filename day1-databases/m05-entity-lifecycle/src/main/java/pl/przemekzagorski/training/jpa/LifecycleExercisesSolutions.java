package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;

/**
 * 🏴‍☠️ Entity Lifecycle Exercises - ROZWIĄZANIA (LIVE DEMO)
 *
 * ═══════════════════════════════════════════════════════════════════
 * 🎯 PRZEZNACZENIE: Plik do LIVE DEMO na szkoleniu
 * ═══════════════════════════════════════════════════════════════════
 *
 * Ten plik zawiera pełne rozwiązania z komentarzami obserwacyjnymi:
 * 🔍 OBSERWUJ - co się dzieje w tym momencie
 * ❓ PYTANIE - pytanie do przemyślenia
 * 💡 WSKAZÓWKA - wyjaśnienie zachowania
 *
 * INSTRUKCJA DLA TRENERA:
 * "Tutaj od razu przejdźmy do rozwiązań i obserwujemy co się dzieje"
 * - Uruchom main()
 * - Obserwuj output w konsoli
 * - Dyskutuj z kursantami o stanach encji
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class LifecycleExercisesSolutions {

    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Entity Lifecycle Exercises - ROZWIĄZANIA");
        System.out.println("═══════════════════════════════════════════════\n");

        emf = Persistence.createEntityManagerFactory("lifecycle-pu");

        try {
            setupTestData();

            exercise1_IdentifyTransientState();
            exercise2_TransitionToManaged();
            exercise3_UnderstandDirtyChecking();
            exercise4_DetachedAndMerge();
            exercise5_RemoveEntity();
            exercise6_ClearContext();

            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 Wszystkie ćwiczenia zakończone!");
            System.out.println("═".repeat(60));

        } finally {
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 1: Rozpoznaj stan TRANSIENT
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Identyfikacja stanu TRANSIENT
     *
     * KLUCZOWE KONCEPCJE:
     * - Obiekt utworzony przez "new" jest TRANSIENT
     * - EntityManager nie wie o jego istnieniu
     * - Nie ma ID (lub ma ręcznie ustawione, ale nie z bazy)
     * - Zmiany w tym obiekcie NIE wpływają na bazę danych
     *
     * TRANSIENT to "surowy" obiekt Java - istnieje tylko w pamięci.
     */
    private static void exercise1_IdentifyTransientState() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 1: Rozpoznaj stan TRANSIENT");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // 🔍 OBSERWUJ: Tworzymy nowego pirata używając konstruktora
            System.out.println("\n   🔍 OBSERWUJ: Tworzę nowego pirata przez 'new'...");
            Pirate newPirate = new Pirate("Blackbeard", "Captain", new BigDecimal("50000"));
            System.out.println("   ✅ Pirat utworzony: " + newPirate);

            // 🔍 OBSERWUJ: Sprawdzamy czy EntityManager zna ten obiekt
            System.out.println("\n   🔍 OBSERWUJ: Sprawdzam stan encji...");
            boolean isManaged = em.contains(newPirate);
            Long pirateId = newPirate.getId();

            System.out.println("   📊 em.contains(newPirate) = " + isManaged);
            System.out.println("   📊 newPirate.getId() = " + pirateId);

            // ❓ PYTANIE: Dlaczego em.contains() zwraca false?
            System.out.println("\n   ❓ PYTANIE: Dlaczego em.contains() zwraca false?");
            System.out.println("   💡 WSKAZÓWKA: Obiekt utworzony przez 'new' jest TRANSIENT");
            System.out.println("   💡 WSKAZÓWKA: EntityManager nie wie o jego istnieniu!");
            System.out.println("   💡 WSKAZÓWKA: Dopiero persist() zmieni stan na MANAGED");

            // ❓ PYTANIE: Dlaczego getId() zwraca null?
            System.out.println("\n   ❓ PYTANIE: Dlaczego getId() zwraca null?");
            System.out.println("   💡 WSKAZÓWKA: ID jest generowane przez bazę danych");
            System.out.println("   💡 WSKAZÓWKA: Obiekt TRANSIENT nie był jeszcze w bazie");
            System.out.println("   💡 WSKAZÓWKA: ID pojawi się dopiero po persist()");

            boolean success = !isManaged && pirateId == null;
            System.out.println("\n   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
            System.out.println("   🎯 WNIOSEK: Obiekt TRANSIENT = nie śledzony + brak ID\n");

        } finally {
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 2: Przejście TRANSIENT → MANAGED
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Zapis encji i zmiana stanu na MANAGED
     *
     * KLUCZOWE KONCEPCJE:
     * - persist() zmienia stan TRANSIENT → MANAGED
     * - Po persist() encja ma przypisane ID
     * - Encja jest teraz śledzona przez EntityManager
     * - Zmiany będą automatycznie zapisywane (dirty checking)
     *
     * UWAGA: persist() planuje INSERT, ale wykonuje go dopiero przy commit()
     * (lub wcześniej przy flush() jeśli ID jest generowane przez IDENTITY)
     */
    private static void exercise2_TransitionToManaged() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 2: TRANSIENT → MANAGED");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // 🔍 OBSERWUJ: Tworzymy pirata - jest TRANSIENT
            System.out.println("\n   🔍 OBSERWUJ: Tworzę pirata (TRANSIENT)...");
            Pirate pirate = new Pirate("Edward Teach", "Captain", new BigDecimal("40000"));
            System.out.println("   📊 Przed persist():");
            System.out.println("      - pirate.getId() = " + pirate.getId());
            System.out.println("      - em.contains(pirate) = " + em.contains(pirate));

            // 🔍 OBSERWUJ: Rozpoczynamy transakcję
            System.out.println("\n   🔍 OBSERWUJ: Rozpoczynam transakcję...");
            em.getTransaction().begin();
            System.out.println("   ✅ Transakcja rozpoczęta");

            // 🔍 OBSERWUJ: persist() zmienia stan na MANAGED
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję em.persist(pirate)...");
            em.persist(pirate);
            System.out.println("   ✅ persist() wykonany!");

            // 🔍 OBSERWUJ: Sprawdzamy co się zmieniło
            boolean isManagedAfterPersist = em.contains(pirate);
            System.out.println("\n   📊 Po persist() (przed commit):");
            System.out.println("      - pirate.getId() = " + pirate.getId());
            System.out.println("      - em.contains(pirate) = " + isManagedAfterPersist);

            // ❓ PYTANIE: Dlaczego ID jest już przypisane PRZED commit()?
            System.out.println("\n   ❓ PYTANIE: Dlaczego ID jest już przypisane PRZED commit()?");
            System.out.println("   💡 WSKAZÓWKA: Hibernate generuje ID przy persist()");
            System.out.println("   💡 WSKAZÓWKA: Ale INSERT do bazy wykona się dopiero przy commit()");
            System.out.println("   💡 WSKAZÓWKA: Dla @GeneratedValue(IDENTITY) INSERT jest od razu!");

            // 🔍 OBSERWUJ: Zatwierdzamy transakcję
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję commit()...");
            em.getTransaction().commit();
            System.out.println("   ✅ Transakcja zatwierdzona - INSERT wykonany w bazie!");

            // ❓ PYTANIE: Co się stanie jeśli teraz zmienię pirate.setBounty()?
            System.out.println("\n   ❓ PYTANIE: Co się stanie jeśli teraz zmienię pirate.setBounty()?");
            System.out.println("   💡 WSKAZÓWKA: Encja jest wciąż MANAGED (dopóki EM otwarty)");
            System.out.println("   💡 WSKAZÓWKA: Ale transakcja zamknięta - zmiana NIE zostanie zapisana!");

            boolean success = pirate.getId() != null && isManagedAfterPersist;
            System.out.println("\n   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
            System.out.println("   🎯 WNIOSEK: persist() → MANAGED + ID przypisane\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 3: Dirty Checking - automatyczna aktualizacja
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Zrozumienie dirty checking
     *
     * KLUCZOWE KONCEPCJE:
     * - Encja MANAGED jest automatycznie śledzona
     * - Hibernate porównuje stan obecny z zapamiętanym "snapshot"
     * - Przy commit() wykrywa zmiany i generuje UPDATE
     * - NIE trzeba wołać żadnej metody "update()"!
     *
     * TO JEST MAGIA JPA: Pracujesz z obiektami jak zwykle,
     * a Hibernate dba o synchronizację z bazą.
     */
    private static void exercise3_UnderstandDirtyChecking() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 3: Dirty Checking - magia aktualizacji");
        System.out.println("═".repeat(60));

        Long testPirateId = getFirstPirateId();
        BigDecimal originalBounty = getPirateBounty(testPirateId);
        BigDecimal newBounty = new BigDecimal("99999");

        System.out.println("\n   📊 Dane testowe:");
        System.out.println("      - Pirat ID: " + testPirateId);
        System.out.println("      - Obecne bounty: " + originalBounty);
        System.out.println("      - Nowe bounty: " + newBounty);

        EntityManager em = emf.createEntityManager();
        try {
            // 🔍 OBSERWUJ: Rozpoczynamy transakcję
            System.out.println("\n   🔍 OBSERWUJ: Rozpoczynam transakcję...");
            em.getTransaction().begin();

            // 🔍 OBSERWUJ: Pobieramy pirata - Hibernate robi "snapshot"
            System.out.println("\n   🔍 OBSERWUJ: Pobieram pirata przez em.find()...");
            Pirate pirate = em.find(Pirate.class, testPirateId);
            System.out.println("   ✅ Pirat pobrany: " + pirate);
            System.out.println("   💡 WSKAZÓWKA: Hibernate zapamiętał 'snapshot' stanu encji!");
            System.out.println("   💡 WSKAZÓWKA: Snapshot = {bounty=" + originalBounty + ", ...}");

            // 🔍 OBSERWUJ: Zmieniamy bounty ZWYKŁYM SETTEREM
            System.out.println("\n   🔍 OBSERWUJ: Zmieniam bounty przez setter...");
            System.out.println("   📝 pirate.setBounty(" + newBounty + ")");
            pirate.setBounty(newBounty);
            System.out.println("   ✅ Setter wykonany!");

            // ❓ PYTANIE: Czy muszę wywołać em.update() lub em.merge()?
            System.out.println("\n   ❓ PYTANIE: Czy muszę wywołać em.update() lub em.merge()?");
            System.out.println("   💡 WSKAZÓWKA: NIE! Encja jest MANAGED - Hibernate śledzi zmiany");
            System.out.println("   💡 WSKAZÓWKA: Przy commit() porówna stan z snapshot");
            System.out.println("   💡 WSKAZÓWKA: Wykryje różnicę i wygeneruje UPDATE automatycznie!");

            // 🔍 OBSERWUJ: Commit - tu dzieje się magia!
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję commit()...");
            System.out.println("   🔍 OBSERWUJ: Hibernate porównuje: snapshot vs obecny stan");
            System.out.println("   🔍 OBSERWUJ: Wykrywa zmianę bounty: " + originalBounty + " → " + newBounty);
            System.out.println("   🔍 OBSERWUJ: Generuje SQL: UPDATE pirates SET bounty=? WHERE id=?");
            em.getTransaction().commit();
            System.out.println("   ✅ UPDATE wykonany automatycznie!");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

        // Weryfikacja
        BigDecimal savedBounty = getPirateBounty(testPirateId);
        boolean success = newBounty.compareTo(savedBounty) == 0;
        System.out.println("\n   📊 Weryfikacja w bazie:");
        System.out.println("      - Bounty w bazie: " + savedBounty);
        System.out.println("   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
        System.out.println("   🎯 WNIOSEK: Dirty Checking = automatyczny UPDATE bez wołania metod!\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 4: DETACHED i merge()
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Użycie merge() dla encji DETACHED
     *
     * KLUCZOWE KONCEPCJE:
     * - Po zamknięciu EntityManager encje stają się DETACHED
     * - Encja DETACHED nadal istnieje w pamięci, ale nie jest śledzona
     * - merge() tworzy NOWĄ kopię encji w stanie MANAGED
     * - ZAWSZE używaj zwróconej wartości merge()!
     *
     * CZĘSTY BŁĄD:
     * em.merge(detached);
     * detached.setX(...);  // ❌ To wciąż DETACHED!
     *
     * POPRAWNIE:
     * Entity managed = em.merge(detached);
     * managed.setX(...);   // ✅ To jest MANAGED
     */
    private static void exercise4_DetachedAndMerge() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 4: DETACHED i merge()");
        System.out.println("═".repeat(60));

        Long testPirateId = getFirstPirateId();

        // 🔍 OBSERWUJ: Krok 1 - Pobieramy pirata i zamykamy EM
        System.out.println("\n   🔍 OBSERWUJ: Pobieram pirata i zamykam EntityManager...");
        EntityManager em1 = emf.createEntityManager();
        Pirate detachedPirate = em1.find(Pirate.class, testPirateId);
        System.out.println("   ✅ Pirat pobrany (MANAGED): " + detachedPirate);

        System.out.println("\n   🔍 OBSERWUJ: Zamykam EntityManager...");
        em1.close();
        System.out.println("   ✅ EntityManager zamknięty");

        // ❓ PYTANIE: Co się stało z encją po zamknięciu EM?
        System.out.println("\n   ❓ PYTANIE: Co się stało z encją po zamknięciu EM?");
        System.out.println("   💡 WSKAZÓWKA: Encja stała się DETACHED!");
        System.out.println("   💡 WSKAZÓWKA: Obiekt wciąż istnieje w pamięci, ale nie jest śledzony");
        System.out.println("   💡 WSKAZÓWKA: Zmiany NIE będą automatycznie zapisywane");

        // 🔍 OBSERWUJ: Krok 2 - Modyfikujemy DETACHED encję
        String newNickname = "The Terror of the Seas";
        System.out.println("\n   🔍 OBSERWUJ: Zmieniam nickname na DETACHED encji...");
        System.out.println("   📝 detachedPirate.setNickname(\"" + newNickname + "\")");
        detachedPirate.setNickname(newNickname);
        System.out.println("   ✅ Zmiana wykonana (tylko w pamięci!)");

        // 🔍 OBSERWUJ: Krok 3 - Używamy merge() aby zapisać zmiany
        System.out.println("\n   🔍 OBSERWUJ: Otwieram nowy EntityManager i używam merge()...");
        EntityManager em2 = emf.createEntityManager();
        try {
            em2.getTransaction().begin();

            // 🔍 OBSERWUJ: merge() zwraca NOWY obiekt!
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję em2.merge(detachedPirate)...");
            Pirate managedPirate = em2.merge(detachedPirate);
            System.out.println("   ✅ merge() wykonany!");

            // 🔍 OBSERWUJ: Sprawdzamy stany obu obiektów
            boolean originalIsManaged = em2.contains(detachedPirate);
            boolean mergedIsManaged = em2.contains(managedPirate);
            boolean sameObject = (detachedPirate == managedPirate);

            System.out.println("\n   📊 Porównanie obiektów:");
            System.out.println("      - em2.contains(detachedPirate) = " + originalIsManaged);
            System.out.println("      - em2.contains(managedPirate) = " + mergedIsManaged);
            System.out.println("      - detachedPirate == managedPirate: " + sameObject);

            // ❓ PYTANIE: Dlaczego to są różne obiekty?
            System.out.println("\n   ❓ PYTANIE: Dlaczego detachedPirate != managedPirate?");
            System.out.println("   💡 WSKAZÓWKA: merge() tworzy NOWĄ kopię encji w stanie MANAGED");
            System.out.println("   💡 WSKAZÓWKA: Oryginalny obiekt pozostaje DETACHED");
            System.out.println("   💡 WSKAZÓWKA: ZAWSZE używaj zwróconej wartości merge()!");

            // ⚠️ CZĘSTY BŁĄD
            System.out.println("\n   ⚠️ CZĘSTY BŁĄD:");
            System.out.println("      em.merge(detached);");
            System.out.println("      detached.setX(...);  // ❌ To wciąż DETACHED - zmiana ZNIKNIE!");
            System.out.println("\n   ✅ POPRAWNIE:");
            System.out.println("      Entity managed = em.merge(detached);");
            System.out.println("      managed.setX(...);   // ✅ To jest MANAGED - zmiana się zapisze!");

            em2.getTransaction().commit();

            boolean success = !originalIsManaged && mergedIsManaged && !sameObject;
            System.out.println("\n   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
            System.out.println("   🎯 WNIOSEK: merge() zwraca NOWY obiekt MANAGED!\n");

        } finally {
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
            em2.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 5: Usuwanie encji - MANAGED → REMOVED
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Poprawne usuwanie encji
     *
     * KLUCZOWE KONCEPCJE:
     * - remove() działa TYLKO na encjach MANAGED
     * - Najpierw musimy pobrać encję (find), potem usunąć (remove)
     * - remove() zmienia stan na REMOVED (encja wciąż jest "zarządzana")
     * - DELETE wykonuje się przy commit()
     *
     * BŁĄD:
     * Pirate p = new Pirate(); p.setId(1L);
     * em.remove(p);  // IllegalArgumentException! To jest TRANSIENT, nie MANAGED
     */
    private static void exercise5_RemoveEntity() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 5: Usuwanie encji (MANAGED → REMOVED)");
        System.out.println("═".repeat(60));

        // 🔍 OBSERWUJ: Przygotowanie - tworzymy pirata do usunięcia
        System.out.println("\n   🔍 OBSERWUJ: Tworzę pirata do usunięcia...");
        Long pirateToDeleteId = createPirateForDeletion();
        System.out.println("   ✅ Pirat utworzony, ID: " + pirateToDeleteId);

        EntityManager em = emf.createEntityManager();
        try {
            // 🔍 OBSERWUJ: Rozpoczynamy transakcję
            System.out.println("\n   🔍 OBSERWUJ: Rozpoczynam transakcję...");
            em.getTransaction().begin();

            // 🔍 OBSERWUJ: Najpierw pobieramy pirata - musi być MANAGED!
            System.out.println("\n   🔍 OBSERWUJ: Pobieram pirata przez em.find()...");
            Pirate pirateToDelete = em.find(Pirate.class, pirateToDeleteId);
            System.out.println("   ✅ Pirat pobrany: " + pirateToDelete);

            // 🔍 OBSERWUJ: Sprawdzamy czy jest MANAGED
            boolean beforeRemove = em.contains(pirateToDelete);
            System.out.println("\n   📊 Przed remove():");
            System.out.println("      - em.contains(pirateToDelete) = " + beforeRemove);

            // ❓ PYTANIE: Dlaczego musimy najpierw pobrać encję?
            System.out.println("\n   ❓ PYTANIE: Dlaczego musimy najpierw pobrać encję?");
            System.out.println("   💡 WSKAZÓWKA: remove() działa TYLKO na encjach MANAGED");
            System.out.println("   💡 WSKAZÓWKA: Nie możemy usunąć encji TRANSIENT lub DETACHED");
            System.out.println("   💡 WSKAZÓWKA: Dlatego: find() → remove() → commit()");

            // 🔍 OBSERWUJ: Usuwamy pirata
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję em.remove(pirateToDelete)...");
            em.remove(pirateToDelete);
            System.out.println("   ✅ remove() wykonany!");

            // 🔍 OBSERWUJ: Sprawdzamy stan po remove()
            boolean afterRemove = em.contains(pirateToDelete);
            System.out.println("\n   📊 Po remove() (przed commit):");
            System.out.println("      - em.contains(pirateToDelete) = " + afterRemove);

            // ❓ PYTANIE: Dlaczego em.contains() wciąż zwraca true?
            System.out.println("\n   ❓ PYTANIE: Dlaczego em.contains() może zwracać true?");
            System.out.println("   💡 WSKAZÓWKA: Encja jest w stanie REMOVED");
            System.out.println("   💡 WSKAZÓWKA: Wciąż jest 'zarządzana' do momentu commit()");
            System.out.println("   💡 WSKAZÓWKA: DELETE wykona się dopiero przy commit()");

            // 🔍 OBSERWUJ: Commit - tu wykonuje się DELETE
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję commit()...");
            System.out.println("   🔍 OBSERWUJ: Hibernate generuje: DELETE FROM pirates WHERE id=?");
            em.getTransaction().commit();
            System.out.println("   ✅ DELETE wykonany w bazie!");

            // Weryfikacja
            boolean pirateExists = checkPirateExists(pirateToDeleteId);
            System.out.println("\n   📊 Weryfikacja:");
            System.out.println("      - Pirat w bazie: " + (pirateExists ? "istnieje" : "usunięty"));

            boolean success = beforeRemove && !pirateExists;
            System.out.println("\n   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
            System.out.println("   🎯 WNIOSEK: remove() wymaga MANAGED encji, DELETE przy commit()\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROZWIĄZANIE 6: clear() - odłączenie wszystkich encji
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * ROZWIĄZANIE: Efekt clear() na persistence context
     *
     * KLUCZOWE KONCEPCJE:
     * - clear() odłącza WSZYSTKIE encje od EntityManager
     * - Wszystkie stają się DETACHED
     * - Zmiany po clear() NIE będą zapisane!
     * - Lazy loading przestaje działać
     *
     * KIEDY UŻYWAĆ clear()?
     * - Batch processing - aby uniknąć memory leak
     * - Przed długim odczytem - aby zwolnić pamięć
     * - NIGDY w środku logiki biznesowej bez przemyślenia!
     */
    private static void exercise6_ClearContext() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 6: clear() - odłączenie wszystkich encji");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            // 🔍 OBSERWUJ: Rozpoczynamy transakcję
            System.out.println("\n   🔍 OBSERWUJ: Rozpoczynam transakcję...");
            em.getTransaction().begin();

            // 🔍 OBSERWUJ: Pobieramy wszystkich piratów - są MANAGED
            System.out.println("\n   🔍 OBSERWUJ: Pobieram wszystkich piratów przez JPQL...");
            List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p", Pirate.class)
                    .getResultList();
            System.out.println("   ✅ Pobrano " + pirates.size() + " piratów");

            // 🔍 OBSERWUJ: Sprawdzamy stan przed clear
            boolean allManagedBefore = pirates.stream().allMatch(em::contains);
            System.out.println("\n   📊 Przed clear():");
            System.out.println("      - Wszyscy MANAGED: " + allManagedBefore);
            System.out.println("      - Liczba encji w Persistence Context: " + pirates.size());

            // 🔍 OBSERWUJ: clear() odłącza WSZYSTKIE encje
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję em.clear()...");
            System.out.println("   🔍 OBSERWUJ: Wszystkie encje zostaną odłączone od EntityManager");
            em.clear();
            System.out.println("   ✅ clear() wykonany!");

            // 🔍 OBSERWUJ: Sprawdzamy stan po clear
            boolean anyManagedAfter = pirates.stream().anyMatch(em::contains);
            System.out.println("\n   📊 Po clear():");
            System.out.println("      - Ktokolwiek MANAGED: " + anyManagedAfter);
            System.out.println("      - Wszyscy stali się DETACHED!");

            // ❓ PYTANIE: Co się stanie jeśli teraz zmodyfikuję encję?
            System.out.println("\n   ❓ PYTANIE: Co się stanie jeśli teraz zmodyfikuję encję?");
            System.out.println("   💡 WSKAZÓWKA: Encje są DETACHED - nie są śledzone");
            System.out.println("   💡 WSKAZÓWKA: Zmiany NIE zostaną zapisane przy commit()");

            // 🔍 OBSERWUJ: Modyfikacja po clear - NIE zostanie zapisana!
            if (!pirates.isEmpty()) {
                System.out.println("\n   🔍 OBSERWUJ: Zmieniam nickname pierwszego pirata...");
                String oldNickname = pirates.get(0).getNickname();
                pirates.get(0).setNickname("ZMIANA_PO_CLEAR");
                System.out.println("   📝 Zmiana: \"" + oldNickname + "\" → \"ZMIANA_PO_CLEAR\"");
                System.out.println("   ⚠️ Ta zmiana ZNIKNIE przy commit()!");
            }

            // 🔍 OBSERWUJ: Commit - zmiana NIE zostanie zapisana
            System.out.println("\n   🔍 OBSERWUJ: Wywołuję commit()...");
            System.out.println("   🔍 OBSERWUJ: Hibernate NIE wygeneruje UPDATE (encje DETACHED)");
            em.getTransaction().commit();
            System.out.println("   ✅ Commit wykonany (bez UPDATE)");

            // Weryfikacja - zmiana nie powinna być zapisana
            String savedNickname = getFirstPirateNickname();
            boolean changeIgnored = !"ZMIANA_PO_CLEAR".equals(savedNickname);

            System.out.println("\n   📊 Weryfikacja w bazie:");
            System.out.println("      - Nickname w bazie: " + savedNickname);
            System.out.println("      - Zmiana zignorowana: " + changeIgnored);

            // ❓ PYTANIE: Kiedy używać clear()?
            System.out.println("\n   ❓ PYTANIE: Kiedy używać clear()?");
            System.out.println("   💡 WSKAZÓWKA: Batch processing - aby uniknąć memory leak");
            System.out.println("   💡 WSKAZÓWKA: Po przetworzeniu dużej liczby encji");
            System.out.println("   💡 WSKAZÓWKA: NIGDY w środku logiki biznesowej!");

            boolean success = allManagedBefore && !anyManagedAfter && changeIgnored;
            System.out.println("\n   " + (success ? "✅ POPRAWNIE!" : "❌ Błąd"));
            System.out.println("   🎯 WNIOSEK: clear() → wszystkie DETACHED → zmiany NIE są śledzone!\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE
    // ═══════════════════════════════════════════════════════════════════════

    private static void setupTestData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Long count = em.createQuery("SELECT COUNT(p) FROM Pirate p", Long.class).getSingleResult();
        if (count == 0) {
            em.persist(new Pirate("Jack Sparrow", "Captain", new BigDecimal("10000")));
            em.persist(new Pirate("Will Turner", "Sailor", new BigDecimal("1000")));
            em.persist(new Pirate("Elizabeth Swann", "Captain", new BigDecimal("5000")));
            System.out.println("   Utworzono testowych piratów\n");
        }

        em.getTransaction().commit();
        em.close();
    }

    private static Long getFirstPirateId() {
        EntityManager em = emf.createEntityManager();
        Long id = em.createQuery("SELECT p.id FROM Pirate p ORDER BY p.id", Long.class)
                .setMaxResults(1)
                .getSingleResult();
        em.close();
        return id;
    }

    private static BigDecimal getPirateBounty(Long id) {
        EntityManager em = emf.createEntityManager();
        Pirate pirate = em.find(Pirate.class, id);
        BigDecimal bounty = pirate != null ? pirate.getBounty() : null;
        em.close();
        return bounty;
    }

    private static Long createPirateForDeletion() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Pirate pirate = new Pirate("Pirate To Delete", "Victim", BigDecimal.ZERO);
        em.persist(pirate);
        em.getTransaction().commit();
        Long id = pirate.getId();
        em.close();
        return id;
    }

    private static boolean checkPirateExists(Long id) {
        EntityManager em = emf.createEntityManager();
        Pirate pirate = em.find(Pirate.class, id);
        em.close();
        return pirate != null;
    }

    private static String getFirstPirateNickname() {
        EntityManager em = emf.createEntityManager();
        Pirate pirate = em.createQuery("SELECT p FROM Pirate p ORDER BY p.id", Pirate.class)
                .setMaxResults(1)
                .getSingleResult();
        String nickname = pirate.getNickname();
        em.close();
        return nickname;
    }
}
