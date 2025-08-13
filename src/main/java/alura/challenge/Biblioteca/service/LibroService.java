package alura.challenge.Biblioteca.service;

import alura.challenge.Biblioteca.exeption.ResourceNotFoundExceptionon;
import alura.challenge.Biblioteca.model.Libro;
import alura.challenge.Biblioteca.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }
    public List<Libro> listarLibrosPorIdioma(String idioma) {
        return libroRepository.findByIdioma(idioma);
    }

    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public Libro obtenerLibroPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptionon("Libro con id " + id + " no encontrado"));
    }

    public Optional<Libro> obtenerLibroPorTitulo(String titulo) {
        return libroRepository.findByTituloIgnoreCase(titulo);
    }

    public Libro actualizarLibro(Long id, Libro libroDetalle) {
        Libro libroExistente= libroRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Libro no encontrado"));
        libroExistente.setTitulo(libroDetalle.getTitulo());
        libroExistente.setIdioma(libroDetalle.getIdioma());
        libroExistente.setNumeroDescargas(libroDetalle.getNumeroDescargas());
        libroExistente.setAutor(libroDetalle.getAutor());
        return libroRepository.save(libroExistente);
    }

    public void eliminarLibro(Long id) {
        Libro libro = obtenerLibroPorId(id);
        libroRepository.delete(libro);
    }
}
