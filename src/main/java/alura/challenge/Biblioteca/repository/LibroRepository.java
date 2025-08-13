package alura.challenge.Biblioteca.repository;

import alura.challenge.Biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloIgnoreCase(String titulo);

    @Query ("select l from Libro l where l.idioma = :idioma ")
    List<Libro> findByIdioma( @Param("idioma") String idioma);
}
