package pl.przemekzagorski.training.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.przemekzagorski.training.jpa.entity.Pirate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla PirateRepository z mockami.
 *
 * KIEDY UŻYWAĆ MOCKÓW?
 * - Gdy chcemy przetestować logikę repozytorium bez bazy
 * - Dla bardzo szybkich testów (brak I/O)
 * - Gdy testujemy edge cases (np. co gdy baza rzuci wyjątek)
 *
 * KIEDY NIE UŻYWAĆ MOCKÓW?
 * - Gdy chcemy przetestować prawdziwe zapytania SQL/JPQL
 * - Gdy logika zależy od zachowania bazy (np. autogenerowane ID)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PirateRepository - Testy Jednostkowe (Mockito)")
class PirateRepositoryUnitTest {

    @Mock
    private EntityManager em;

    @Mock
    @SuppressWarnings("unused")
    private TypedQuery<Pirate> typedQuery;

    @Mock
    @SuppressWarnings("unused")
    private TypedQuery<Long> countQuery;

    @InjectMocks
    private PirateRepository repository;

    // ========================================================================
    // Testy findById
    // ========================================================================

    @Test
    @DisplayName("findById - should return pirate when found")
    void shouldFindById() {
        // Arrange
        Pirate expected = new Pirate("Jack Sparrow", "Captain", new BigDecimal("100000"));
        when(em.find(Pirate.class, 1L)).thenReturn(expected);

        // Act
        Optional<Pirate> result = repository.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Jack Sparrow");

        // Verify interaction
        verify(em).find(Pirate.class, 1L);
        verifyNoMoreInteractions(em);
    }

    @Test
    @DisplayName("findById - should return empty when not found")
    void shouldReturnEmptyWhenNotFound() {
        // Arrange
        when(em.find(Pirate.class, 999L)).thenReturn(null);

        // Act
        Optional<Pirate> result = repository.findById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(em).find(Pirate.class, 999L);
    }

    @Test
    @DisplayName("findById - should throw when id is null")
    void shouldThrowWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID cannot be null");

        // Verify no interaction with EM
        verifyNoInteractions(em);
    }

    // ========================================================================
    // Testy save
    // ========================================================================

    @Test
    @DisplayName("save - should persist new pirate")
    void shouldPersistNewPirate() {
        // Arrange
        Pirate newPirate = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        // ID jest null - więc persist

        // Act
        Pirate result = repository.save(newPirate);

        // Assert
        assertThat(result).isSameAs(newPirate);
        verify(em).persist(newPirate);
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("save - should merge existing pirate")
    void shouldMergeExistingPirate() {
        // Arrange
        Pirate existing = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        existing.setId(1L);  // Ma ID - więc merge

        Pirate merged = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        merged.setId(1L);
        when(em.merge(existing)).thenReturn(merged);

        // Act
        Pirate result = repository.save(existing);

        // Assert
        assertThat(result).isSameAs(merged);
        verify(em).merge(existing);
        verify(em, never()).persist(any());
    }

    @Test
    @DisplayName("save - should throw when pirate is null")
    void shouldThrowWhenPirateIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pirate cannot be null");

        verifyNoInteractions(em);
    }

    // ========================================================================
    // Testy findByName
    // ========================================================================

    @Test
    @DisplayName("findByName - should return pirate when found")
    void shouldFindByName() {
        // Arrange
        Pirate expected = new Pirate("Jack Sparrow", "Captain", BigDecimal.ZERO);

        @SuppressWarnings("unchecked")
        TypedQuery<Pirate> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Pirate.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("name", "Jack Sparrow")).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(expected);

        // Act
        Optional<Pirate> result = repository.findByName("Jack Sparrow");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Jack Sparrow");
    }

    @Test
    @DisplayName("findByName - should return empty when NoResultException")
    void shouldReturnEmptyWhenNoResult() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<Pirate> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Pirate.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("name", "Unknown")).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Optional<Pirate> result = repository.findByName("Unknown");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByName - should throw when name is null")
    void shouldThrowWhenNameIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> repository.findByName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null");

        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("findByName - should throw when name is blank")
    void shouldThrowWhenNameIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> repository.findByName("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or blank");

        verifyNoInteractions(em);
    }

    // ========================================================================
    // Testy delete
    // ========================================================================

    @Test
    @DisplayName("delete - should remove managed pirate")
    void shouldRemoveManagedPirate() {
        // Arrange
        Pirate pirate = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        when(em.contains(pirate)).thenReturn(true);

        // Act
        repository.delete(pirate);

        // Assert
        verify(em).remove(pirate);
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("delete - should merge then remove detached pirate")
    void shouldMergeThenRemoveDetachedPirate() {
        // Arrange
        Pirate detached = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        Pirate merged = new Pirate("Jack", "Captain", BigDecimal.ZERO);

        when(em.contains(detached)).thenReturn(false);
        when(em.merge(detached)).thenReturn(merged);

        // Act
        repository.delete(detached);

        // Assert
        verify(em).merge(detached);
        verify(em).remove(merged);
    }

    @Test
    @DisplayName("delete - should throw when pirate is null")
    void shouldThrowWhenDeleteNull() {
        // Act & Assert
        assertThatThrownBy(() -> repository.delete(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pirate cannot be null");

        verifyNoInteractions(em);
    }

    // ========================================================================
    // Testy count
    // ========================================================================

    @Test
    @DisplayName("count - should return count from query")
    void shouldReturnCount() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(5L);

        // Act
        long count = repository.count();

        // Assert
        assertThat(count).isEqualTo(5L);
    }

    // ========================================================================
    // Testy existsById
    // ========================================================================

    @Test
    @DisplayName("existsById - should return true when found")
    void shouldReturnTrueWhenExists() {
        // Arrange
        Pirate pirate = new Pirate("Jack", "Captain", BigDecimal.ZERO);
        when(em.find(Pirate.class, 1L)).thenReturn(pirate);

        // Act
        boolean exists = repository.existsById(1L);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById - should return false when not found")
    void shouldReturnFalseWhenNotExists() {
        // Arrange
        when(em.find(Pirate.class, 999L)).thenReturn(null);

        // Act
        boolean exists = repository.existsById(999L);

        // Assert
        assertThat(exists).isFalse();
    }
}
