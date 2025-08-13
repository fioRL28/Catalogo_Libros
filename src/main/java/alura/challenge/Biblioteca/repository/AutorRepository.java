package alura.challenge.Biblioteca.repository;

import alura.challenge.Biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {

    Optional<Autor> findByNombre(String nombre);

    @Query("select a from Autor a left join fetch a.libros where (a.anioFallecimiento is null or a.anioFallecimiento>:ano) and a.anioNacimiento <= :ano")
    List<Autor> finAutoresVivosEnAnoConLibros(@Param("ano") int ano);

    @Query("select a from Autor a left join fetch a.libros" )
    List<Autor> findAllConLibros();





}
