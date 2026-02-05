# JPA - Java Persistence API

---

## Czym jest JPA?

**Specyfikacja ORM (Object-Relational Mapping)**
- Mapowanie obiektów Java ↔ tabele w bazie danych
- Automatyczne generowanie SQL
- Zarządzanie stanem obiektów (Persistence Context)

**JPA = Interfejs, Hibernate = Implementacja**
- JPA - standard (javax.persistence / jakarta.persistence)
- Hibernate - najpopularniejsza implementacja JPA
- EclipseLink, OpenJPA - inne implementacje

---

## JDBC vs JPA

| JDBC | JPA |
|------|-----|
| Ręczne SQL | Automatyczne SQL |
| Ręczny mapping ResultSet → Object | Automatyczny mapping |
| Kod powtarzalny | Mniej kodu |
| Pełna kontrola | Abstrakcja |
| Trudne zarządzanie relacjami | Automatyczne relacje (@OneToMany, @ManyToOne) |

**Przykład:**

**JDBC (10 linii):**
```java
String sql = "SELECT * FROM pirates WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setLong(1, id);
ResultSet rs = stmt.executeQuery();
if (rs.next()) {
    Pirate pirate = new Pirate();
    pirate.setId(rs.getLong("id"));
    pirate.setName(rs.getString("name"));
    // ...
}
```

**JPA (1 linia):**
```java
Pirate pirate = entityManager.find(Pirate.class, id);
```

---

## Kluczowe komponenty JPA

**@Entity** - klasa mapowana na tabelę
```java
@Entity
@Table(name = "pirates")
public class Pirate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String rank;
    private Integer bounty;
}
```

**EntityManager** - zarządzanie encjami
```java
EntityManager em = entityManagerFactory.createEntityManager();
Pirate pirate = em.find(Pirate.class, 1L);  // SELECT
em.persist(pirate);  // INSERT
em.merge(pirate);    // UPDATE
em.remove(pirate);   // DELETE
```

**persistence.xml** - konfiguracja
```xml
<persistence-unit name="pirates-pu">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <properties>
        <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:pirates"/>
        <property name="hibernate.show_sql" value="true"/>
    </properties>
</persistence-unit>
```

---

## Wskazówka dla trenera
**Czas:** 15 minut

**Co mówię:**
- "JPA to specyfikacja ORM - mapowanie obiektów na tabele."
- "Hibernate to najpopularniejsza implementacja JPA (używana przez Spring Boot)."
- "JPA automatyzuje SQL - mniej kodu, ale mniej kontroli niż JDBC."
- "EntityManager to odpowiednik Connection w JDBC."

**Co pokazuję:**
- Klasę `Pirate` z adnotacjami (@Entity, @Id, @GeneratedValue)
- `persistence.xml` - konfiguracja
- `JpaDemo.java` - podstawowe operacje (find, persist, merge, remove)
- Uruchamiam demo, pokazuję wygenerowane SQL (hibernate.show_sql=true)

**Następny krok:** Slajd `10-entity-lifecycle.md`

