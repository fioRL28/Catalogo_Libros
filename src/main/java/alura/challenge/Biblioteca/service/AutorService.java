package alura.challenge.Biblioteca.service;

import alura.challenge.Biblioteca.model.Autor;
import alura.challenge.Biblioteca.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutores(){
        return autorRepository.findAllConLibros();
    }

    public List<Autor> listarAutoresVivoEnAno(int ano){
        return autorRepository.finAutoresVivosEnAnoConLibros(ano);
    }

    public Autor crearAutor(Autor autor){
        return autorRepository.save(autor);
    }
    public Optional<Autor> obtenerAutorPorId(Long id){
        return autorRepository.findById(id);
    }

    public Optional <Autor> obtenerAutorPorNombre(String nombre){
        return autorRepository.findByNombre(nombre);
    }

    public Autor actualizarAutor(Long id, Autor autorDetalle){
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        autor.setNombre(autorDetalle.getNombre());
        autor.setAnioNacimiento(autorDetalle.getAnioNacimiento());
        autor.setAnioFallecimiento(autorDetalle.getAnioFallecimiento());

        return autorRepository.save(autor);

    }

    public void eliminarAutor(Long id){
        autorRepository.deleteById(id);
    }

}
