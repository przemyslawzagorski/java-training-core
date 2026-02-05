package pl.przemekzagorski.training.hibernate.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Autor książek - do demonstracji N+1 i Lazy/Eager.
 *
 * W kontekście pirackim: Autor = Kapitan, Book = Przygoda/Rejs
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Relacja OneToMany z LAZY loading (domyślne).
     *
     * LAZY = książki są ładowane DOPIERO gdy wywołasz getBooks()
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "'}";
    }
}

