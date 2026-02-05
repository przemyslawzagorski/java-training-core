package pl.przemekzagorski.training.hibernate.entity;

import jakarta.persistence.*;

/**
 * Książka - w kontekście pirackim: Przygoda/Rejs kapitana.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "publication_year")
    private Integer year;

    /**
     * Relacja ManyToOne - domyślnie EAGER!
     *
     * EAGER = autor jest ładowany ZAWSZE razem z książką
     */
    @ManyToOne(fetch = FetchType.LAZY)  // Zmieniamy na LAZY dla wydajności
    @JoinColumn(name = "author_id")
    private Author author;

    public Book() {
    }

    public Book(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', year=" + year + "}";
    }
}

