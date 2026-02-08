package pl.przemekzagorski.training.jpa;

import jakarta.persistence.*;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;

/**
 * 🏴‍☠️ Ćwiczenia: Cykl życia encji JPA
 *
 * ═══════════════════════════════════════════════════════════════════
 * INSTRUKCJA DLA KURSANTA:
 * ═══════════════════════════════════════════════════════════════════
 *
 * Każde ćwiczenie testuje Twoje rozumienie stanów encji JPA:
 * - TRANSIENT (NEW)
 * - MANAGED (PERSISTENT)
 * - DETACHED
 * - REMOVED
 *
 * Uzupełnij kod w miejscach TODO, a następnie uruchom main().
 * Program zweryfikuje poprawność Twoich rozwiązań.
 *
 * KLUCZOWE KONCEPCJE:
 * - em.contains(entity) - czy encja jest MANAGED?
 * - em.persist() - TRANSIENT → MANAGED
 * - em.find() - pobiera MANAGED encję
 * - em.detach() - MANAGED → DETACHED
 * - em.merge() - DETACHED → MANAGED (zwraca NOWY obiekt!)
 * - em.remove() - MANAGED → REMOVED
 *
 * ═══════════════════════════════════════════════════════════════════
 */
public class LifecycleExercises {

    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        System.out.println("🏴‍☠️ Entity Lifecycle Exercises");
        System.out.println("═══════════════════════════════════\n");

        emf = Persistence.createEntityManagerFactory("lifecycle-pu");

        try {
            // Przygotowanie danych
            setupTestData();

            // Uruchomienie ćwiczeń
            exercise1_IdentifyTransientState();
            exercise2_TransitionToManaged();
            exercise3_UnderstandDirtyChecking();
            exercise4_DetachedAndMerge();
            exercise5_RemoveEntity();
            exercise6_ClearContext();

            System.out.println("\n" + "═".repeat(60));
            System.out.println("🎉 Wszystkie ćwiczenia wykonane!");
            System.out.println("═".repeat(60));

        } finally {
            emf.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 1: Rozpoznaj stan TRANSIENT
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Sprawdź, czy nowo utworzony obiekt jest TRANSIENT.
     *
     * Zadanie:
     * 1. Utwórz EntityManager
     * 2. Utwórz nowego pirata (obiekt Pirate)
     * 3. Sprawdź czy em.contains(pirate) zwraca false
     * 4. Sprawdź czy pirate.getId() zwraca null
     *
     * Pytanie: Dlaczego nowy obiekt NIE jest zarządzany przez EntityManager?
     */
    private static void exercise1_IdentifyTransientState() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 1: Rozpoznaj stan TRANSIENT");
        System.out.println("═".repeat(60));

        // 🔴 BREAKPOINT 1: Ustaw tutaj - PRZED utworzeniem EntityManager
        // 👁️ OBSERWUJ: emf jest otwarty, zaraz utworzymy EntityManager
        EntityManager em = emf.createEntityManager();

        try {
            // 🔴 BREAKPOINT 2: PRZED utworzeniem pirata
            // 👁️ OBSERWUJ: em jest pusty (nie zarządza żadnymi encjami)
            // 💡 ZADANIE: W Evaluate sprawdź: em.isOpen() - powinno być true

            // TODO: Utwórz nowego pirata - NIE używaj persist()
            Pirate newPirate = null; // <-- Utwórz pirata: new Pirate("Blackbeard", "Captain", new BigDecimal("50000"))

            // 🔴 BREAKPOINT 3: PO utworzeniu pirata
            // 👁️ OBSERWUJ w Variables:
            //    - newPirate - rozwiń i zobacz wszystkie pola
            //    - newPirate.id = null (nie ma ID!)
            //    - newPirate.name = "Blackbeard"
            //    - newPirate.rank = "Captain"
            //    - newPirate.bounty = 50000
            // 💡 ZADANIE: W Evaluate sprawdź: em.contains(newPirate)
            //    Wynik: false - EntityManager NIE WIE o tym obiekcie!
            // 💡 KLUCZOWA OBSERWACJA: To jest stan TRANSIENT (NEW)
            //    - Obiekt istnieje w pamięci Java (utworzony przez "new")
            //    - NIE istnieje w bazie danych
            //    - EntityManager o nim NIE WIE (em.contains() = false)
            //    - Nie ma ID (id = null)
            //    - Zmiany w tym obiekcie NIE wpłyną na bazę danych

            // TODO: Sprawdź stan encji
            boolean isManaged = true;  // <-- Użyj em.contains(newPirate)
            Long pirateId = 1L;        // <-- Użyj newPirate.getId()

            // 🔴 BREAKPOINT 4: Po sprawdzeniu stanu
            // 👁️ OBSERWUJ:
            //    - isManaged = false (encja NIE jest zarządzana)
            //    - pirateId = null (brak ID)
            // 💡 PYTANIE: Co się stanie jeśli zamkniemy em bez persist()?
            //    Odpowiedź: Obiekt zniknie! Nie zostanie zapisany w bazie.
            //    Będzie tylko "śmieciem" w pamięci Java (garbage collector go usunie)
            // 💡 PYTANIE: Jak zmienić stan z TRANSIENT na MANAGED?
            //    Odpowiedź: Użyj em.persist(newPirate) w transakcji!

            // Weryfikacja
            System.out.println("   Nowy pirat: " + newPirate);
            System.out.println("   em.contains() = " + isManaged);
            System.out.println("   getId() = " + pirateId);

            boolean success = newPirate != null && !isManaged && pirateId == null;
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Obiekt jest TRANSIENT - nie jest śledzony przez EM\n");

        } finally {
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 2: Przejście TRANSIENT → MANAGED
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Zapisz encję i sprawdź zmianę stanu.
     *
     * Zadanie:
     * 1. Utwórz nowego pirata (TRANSIENT)
     * 2. Rozpocznij transakcję
     * 3. Użyj persist() - obserwuj zmianę stanu
     * 4. Zatwierdź transakcję
     * 5. Sprawdź czy ID zostało przypisane
     */
    private static void exercise2_TransitionToManaged() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 2: TRANSIENT → MANAGED");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();

        try {
            // 1. Nowy pirat - TRANSIENT
            Pirate pirate = new Pirate("Edward Teach", "Captain", new BigDecimal("40000"));
            System.out.println("   Przed persist():");
            System.out.println("   - ID = " + pirate.getId());
            System.out.println("   - em.contains() = " + em.contains(pirate));

            // TODO: Rozpocznij transakcję
            // em.getTransaction().begin();

            // TODO: Użyj persist() aby zmienić stan na MANAGED
            // em.persist(pirate);

            boolean isManagedAfterPersist = false; // <-- Sprawdź: em.contains(pirate)
            System.out.println("\n   Po persist() (przed commit):");
            System.out.println("   - ID = " + pirate.getId());
            System.out.println("   - em.contains() = " + isManagedAfterPersist);

            // TODO: Zatwierdź transakcję
            // em.getTransaction().commit();

            // Weryfikacja
            boolean success = pirate.getId() != null && isManagedAfterPersist;
            System.out.println("\n   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Po persist() encja ma ID i jest MANAGED\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 3: Dirty Checking - automatyczna aktualizacja
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Sprawdź, że zmiany w encji MANAGED są automatycznie zapisywane.
     *
     * Zadanie:
     * 1. Pobierz pirata z bazy (będzie MANAGED)
     * 2. Zmień jego bounty używając settera
     * 3. NIE wołaj żadnej metody "update"!
     * 4. Zatwierdź transakcję - Hibernate SAM wykryje zmianę
     * 5. Zweryfikuj, że zmiana została zapisana
     */
    private static void exercise3_UnderstandDirtyChecking() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 3: Dirty Checking - magia aktualizacji");
        System.out.println("═".repeat(60));

        // Najpierw sprawdźmy obecne bounty
        Long testPirateId = getFirstPirateId();
        BigDecimal originalBounty = getPirateBounty(testPirateId);
        BigDecimal newBounty = new BigDecimal("99999");

        System.out.println("   Pirat ID: " + testPirateId);
        System.out.println("   Obecne bounty: " + originalBounty);
        System.out.println("   Nowe bounty: " + newBounty);

        EntityManager em = emf.createEntityManager();
        try {
            // TODO: Rozpocznij transakcję
            // em.getTransaction().begin();

            // TODO: Pobierz pirata - będzie MANAGED
            Pirate pirate = null; // <-- em.find(Pirate.class, testPirateId);

            // TODO: Zmień bounty - NIE wołaj żadnego "update()"!
            // pirate.setBounty(newBounty);

            // TODO: Zatwierdź transakcję - Hibernate SAM wykona UPDATE
            // em.getTransaction().commit();

            System.out.println("\n   Zmieniono bounty bez wołania update()!");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

        // Weryfikacja - sprawdź wartość w bazie
        BigDecimal savedBounty = getPirateBounty(testPirateId);
        boolean success = newBounty.compareTo(savedBounty) == 0;
        System.out.println("   Bounty w bazie: " + savedBounty);
        System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
        System.out.println("   → Dirty checking automatycznie wykrył zmianę!\n");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 4: DETACHED i merge()
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Zrozum różnicę między encją DETACHED a MANAGED po merge().
     *
     * Zadanie:
     * 1. Pobierz pirata i zamknij EntityManager (encja staje się DETACHED)
     * 2. Zmodyfikuj encję DETACHED
     * 3. Użyj merge() aby przywrócić do MANAGED
     * 4. WAŻNE: merge() zwraca NOWY obiekt - używaj zwróconej wartości!
     */
    private static void exercise4_DetachedAndMerge() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 4: DETACHED i merge()");
        System.out.println("═".repeat(60));

        Long testPirateId = getFirstPirateId();

        // Krok 1: Pobierz pirata i zamknij EM - stanie się DETACHED
        // 🔴 BREAKPOINT 1: PRZED find()
        // 👁️ OBSERWUJ: em1 jest otwarty, zaraz pobierzemy pirata
        EntityManager em1 = emf.createEntityManager();
        Pirate detachedPirate = em1.find(Pirate.class, testPirateId);

        // 🔴 BREAKPOINT 2: PO find(), PRZED close()
        // 👁️ OBSERWUJ w Variables:
        //    - detachedPirate - ma wszystkie dane z bazy
        //    - detachedPirate.id - ma wartość (np. 1)
        // 💡 ZADANIE: Sprawdź em1.contains(detachedPirate) - zwróci true (MANAGED)

        em1.close();

        // 🔴 BREAKPOINT 3: PO close()
        // 👁️ OBSERWUJ: detachedPirate wciąż istnieje w pamięci!
        // 💡 ZADANIE: Spróbuj em1.contains(detachedPirate) - rzuci IllegalStateException!
        //    Dlaczego? Bo em1 jest zamknięty
        // 💡 KLUCZOWA OBSERWACJA: Encja jest teraz DETACHED
        //    - Istnieje w pamięci Java (możemy używać getterów/setterów)
        //    - Istnieje w bazie danych
        //    - ALE EntityManager o niej nie wie (bo jest zamknięty)
        //    - Zmiany w detachedPirate NIE będą automatycznie zapisane!

        System.out.println("   Pirat pobrany i EM zamknięty: " + detachedPirate);

        // Krok 2: Modyfikuj DETACHED encję
        String newNickname = "The Terror of the Seas";
        detachedPirate.setNickname(newNickname);

        // 🔴 BREAKPOINT 4: PO setNickname()
        // 👁️ OBSERWUJ: detachedPirate.nickname - zmieniony w pamięci
        // 💡 PYTANIE: Czy zmiana została zapisana w bazie? NIE!
        //    Encja jest DETACHED - nie jest śledzona przez żaden EntityManager

        System.out.println("   Zmieniono nickname na: " + newNickname);

        // Krok 3: Użyj merge() aby zapisać zmiany
        EntityManager em2 = emf.createEntityManager();
        try {
            em2.getTransaction().begin();

            // 🔴 BREAKPOINT 5: PRZED merge()
            // 👁️ OBSERWUJ: em2.contains(detachedPirate) = false (nie jest zarządzany)
            // 💡 ZADANIE: Sprawdź em2.contains(detachedPirate) w Evaluate

            // TODO: Użyj merge() - PAMIĘTAJ: zwraca NOWY obiekt!
            Pirate managedPirate = null; // <-- em2.merge(detachedPirate);

            // 🔴 BREAKPOINT 6: PO merge()
            // 👁️ OBSERWUJ w Variables: Mamy DWIE referencje!
            //    - detachedPirate (oryginalny obiekt)
            //    - managedPirate (zwrócony przez merge())
            // 💡 ZADANIE: Sprawdź w Evaluate:
            //    - em2.contains(detachedPirate) - zwróci false!
            //    - em2.contains(managedPirate) - zwróci true!
            // 💡 KLUCZOWA OBSERWACJA: merge() zwraca NOWY obiekt!
            //    - detachedPirate wciąż jest DETACHED (nie jest zarządzany)
            //    - managedPirate jest MANAGED (zarządzany przez em2)
            //    - To są DWA RÓŻNE obiekty w pamięci!
            // 💡 CZĘSTY BŁĄD POCZĄTKUJĄCYCH:
            //    ❌ ŹLE: em.merge(pirate); pirate.setName("X");
            //       Zmiana NIE zostanie zapisana! (pirate wciąż DETACHED)
            //    ✅ DOBRZE: pirate = em.merge(pirate); pirate.setName("X");
            //       Teraz pirate wskazuje na obiekt MANAGED - zmiana zostanie zapisana!

            // TODO: Sprawdź stany obu obiektów
            boolean originalIsManaged = true; // <-- em2.contains(detachedPirate) - oryginalny
            boolean mergedIsManaged = false;  // <-- em2.contains(managedPirate) - zwrócony

            System.out.println("\n   Po merge():");
            System.out.println("   - Oryginalny (detachedPirate) em.contains() = " + originalIsManaged);
            System.out.println("   - Zwrócony (managedPirate) em.contains() = " + mergedIsManaged);

            // 🔴 BREAKPOINT 7: PRZED commit()
            // 💡 ZADANIE: Sprawdź czy detachedPirate == managedPirate
            //    Wynik: false - to są RÓŻNE obiekty!

            em2.getTransaction().commit();

            // 🔴 BREAKPOINT 8: PO commit()
            // 👁️ OBSERWUJ: Logi SQL - zobaczysz UPDATE z nowym nickname
            // 💡 KLUCZOWA OBSERWACJA: merge() skopiował zmiany do MANAGED encji
            //    i Hibernate zapisał je do bazy przy commit()

            boolean success = !originalIsManaged && mergedIsManaged && managedPirate != null;
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → merge() zwraca NOWY obiekt MANAGED!\n");

        } finally {
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
            em2.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 5: Usuwanie encji - MANAGED → REMOVED
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Usuń encję poprawnie (najpierw find, potem remove).
     *
     * Zadanie:
     * 1. Utwórz nowego pirata (do usunięcia)
     * 2. Pobierz go (aby był MANAGED)
     * 3. Użyj remove() - zmieni stan na REMOVED
     * 4. Zatwierdź transakcję - DELETE wykona się
     *
     * ⚠️ BŁĄD: Nie można usunąć encji TRANSIENT lub DETACHED!
     */
    private static void exercise5_RemoveEntity() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 5: Usuwanie encji (MANAGED → REMOVED)");
        System.out.println("═".repeat(60));

        // Najpierw utwórz pirata do usunięcia
        Long pirateToDeleteId = createPirateForDeletion();
        System.out.println("   Utworzono pirata do usunięcia, ID: " + pirateToDeleteId);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // TODO: Pobierz pirata - musi być MANAGED aby go usunąć
            Pirate pirateToDelete = null; // <-- em.find(Pirate.class, pirateToDeleteId);

            // TODO: Sprawdź czy jest MANAGED
            boolean beforeRemove = false; // <-- em.contains(pirateToDelete);
            System.out.println("   Przed remove(): em.contains() = " + beforeRemove);

            // TODO: Usuń pirata
            // em.remove(pirateToDelete);

            // Sprawdź stan po remove() - wciąż technicznie "zarządzany" do commit
            boolean afterRemove = em.contains(pirateToDelete);
            System.out.println("   Po remove(): em.contains() = " + afterRemove);

            // TODO: Zatwierdź - tu wykona się DELETE
            em.getTransaction().commit();

            // Weryfikacja
            boolean pirateExists = checkPirateExists(pirateToDeleteId);
            boolean success = beforeRemove && !pirateExists && pirateToDelete != null;
            System.out.println("   Pirat w bazie po commit: " + (pirateExists ? "istnieje" : "usunięty"));
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Encja usunięta z bazy!\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ĆWICZENIE 6: clear() - odłączenie wszystkich encji
    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Sprawdź efekt em.clear() na wszystkie zarządzane encje.
     *
     * Zadanie:
     * 1. Pobierz kilka encji (wszystkie będą MANAGED)
     * 2. Wywołaj em.clear()
     * 3. Sprawdź, że wszystkie encje stały się DETACHED
     * 4. Zrozum, że zmiany po clear() NIE będą zapisane!
     */
    private static void exercise6_ClearContext() {
        System.out.println("═".repeat(60));
        System.out.println("📝 ĆWICZENIE 6: clear() - odłączenie wszystkich encji");
        System.out.println("═".repeat(60));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Pobierz wszystkich piratów - będą MANAGED
            List<Pirate> pirates = em.createQuery("SELECT p FROM Pirate p", Pirate.class)
                    .getResultList();

            System.out.println("   Pobrano " + pirates.size() + " piratów");

            // Sprawdź stan przed clear
            boolean allManagedBefore = pirates.stream().allMatch(em::contains);
            System.out.println("   Przed clear(): wszyscy MANAGED = " + allManagedBefore);

            // TODO: Wywołaj em.clear() - odłączy WSZYSTKIE encje
            // em.clear();

            // TODO: Sprawdź stan po clear
            boolean anyManagedAfter = true; // <-- pirates.stream().anyMatch(em::contains);
            System.out.println("   Po clear(): ktokolwiek MANAGED = " + anyManagedAfter);

            // Modyfikacja po clear - NIE zostanie zapisana!
            if (!pirates.isEmpty()) {
                pirates.get(0).setNickname("ZMIANA_PO_CLEAR");
                System.out.println("   Zmodyfikowano pirata po clear()");
            }

            em.getTransaction().commit();

            // Weryfikacja - zmiana nie powinna być zapisana
            String savedNickname = getFirstPirateNickname();
            boolean changeIgnored = !"ZMIANA_PO_CLEAR".equals(savedNickname);
            boolean success = allManagedBefore && !anyManagedAfter && changeIgnored;

            System.out.println("   Nickname w bazie: " + savedNickname);
            System.out.println("   Status: " + (success ? "✅ POPRAWNIE!" : "❌ Sprawdź rozwiązanie"));
            System.out.println("   → Po clear() encje są DETACHED - zmiany NIE są zapisywane!\n");

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODY POMOCNICZE (NIE MODYFIKUJ)
    // ═══════════════════════════════════════════════════════════════════════

    private static void setupTestData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Dodaj testowych piratów jeśli baza jest pusta
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
