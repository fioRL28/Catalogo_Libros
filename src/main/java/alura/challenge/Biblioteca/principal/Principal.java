package alura.challenge.Biblioteca.principal;

import alura.challenge.Biblioteca.dto.AutorDTO;
import alura.challenge.Biblioteca.dto.LibroDTO;
import alura.challenge.Biblioteca.dto.RespuestaLibroDTO;
import alura.challenge.Biblioteca.model.Autor;
import alura.challenge.Biblioteca.model.Libro;
import alura.challenge.Biblioteca.service.AutorService;
import alura.challenge.Biblioteca.service.ConsumoAPI;
import alura.challenge.Biblioteca.service.ConvierteDatos;
import alura.challenge.Biblioteca.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;


@Component
public class Principal {

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    private static final String BASE_URL = "https://gutendex.com/books/";
    private final Scanner teclado = new Scanner(System.in);



    public void muestraMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("---CATÁLOGO DE LITERATURA---");
            System.out.println("""
                    1 - Buscar libro por título
                    2 - Listar Libros registrados
                    3 - Listar Autores registrados
                    4 - Listar autores vivos en determinado año
                    5 - Listar Libros por idioma
                    0 - Salir
                    """);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibroRegistrado();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        String titulo = teclado.nextLine();
        try {
            String encodedTitulo = URLEncoder.encode(titulo, "UTF-8");
            String json = consumoAPI.obtnerDatos(BASE_URL + "?search=" + encodedTitulo);
            RespuestaLibroDTO respuestaLibroDTO = convierteDatos.obtenerDatos(json, RespuestaLibroDTO.class);
            List<LibroDTO> librosDTO = respuestaLibroDTO.getLibros();

            if (librosDTO.isEmpty()) {
                System.out.println("Libro no encontrado");
            } else {
                boolean libroRegistrado = false;
                for (LibroDTO libroDTO : librosDTO) {
                    if (libroDTO.getTitulo().equalsIgnoreCase(titulo)) {
                        Optional<Libro> libroExistente = libroService.obtenerLibroPorTitulo(titulo);
                        if (libroExistente.isPresent()) {
                            System.out.println("Detalle: Clave (titulo)=(" + titulo + ") ya existe");
                            System.out.println("No se puede registrar el mismo libro más de una vez");
                            libroRegistrado = true;
                            break;
                        } else {
                            Libro libro = new Libro();
                            libro.setTitulo(libroDTO.getTitulo());
                            libro.setIdioma(libroDTO.getIdiomas().get(0));
                            libro.setNumeroDescargas(libroDTO.getNumeroDescargas());

                            // Buscar o crear autor
                            AutorDTO primerAutorDTO = libroDTO.getAutores().get(0);
                            Autor autor = autorService.obtenerAutorPorNombre(primerAutorDTO.getNombre())
                                    .orElseGet(() -> {
                                        Autor nuevoAutor = new Autor();
                                        nuevoAutor.setNombre(primerAutorDTO.getNombre());
                                        nuevoAutor.setAnioNacimiento(primerAutorDTO.getAnioNacimiento());
                                        nuevoAutor.setAnioFallecimiento(primerAutorDTO.getAnioFallecimiento());
                                        return autorService.crearAutor(nuevoAutor);
                                    });

                            libro.setAutor(autor);
                            libroService.crearLibro(libro);
                            System.out.println("Libro registrado: " + libro.getTitulo());
                            mostrarDetallesLibro(libroDTO);
                            libroRegistrado = true;
                            break;
                        }
                    }
                }
                if (!libroRegistrado) {
                    System.out.println("No se encontró un libro con ese título");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener datos de la API: " + e.getMessage());
        }
    }

    private void mostrarDetallesLibro(LibroDTO libroDTO) {
        System.out.println("-------LIBRO-------");
        System.out.println("Titulo: " + libroDTO.getTitulo());
        System.out.println("Autor: " + (libroDTO.getAutores().isEmpty() ? "Desconocido" : libroDTO.getAutores().get(0).getNombre()));
        System.out.println("Idioma: " + libroDTO.getIdiomas().get(0));
        System.out.println("Número de Descargas: " + libroDTO.getNumeroDescargas());
    }

    private void listarLibroRegistrado() {
        libroService.listarLibros().forEach(libro -> {
            System.out.println("-------LIBROS REGISTRADOS-------");
            System.out.println("Titulo: " + libro.getTitulo());
            System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : ""));
            System.out.println("Idioma: " + libro.getIdioma());
            System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
        });
    }

    private void listarAutoresRegistrados() {
        autorService.listarAutores().forEach(autor -> {
            System.out.println("-------AUTORES REGISTRADOS-------");
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getAnioNacimiento());
            System.out.println("Fecha de fallecimiento: " + autor.getAnioFallecimiento());
            String libros = autor.getLibros().stream()
                    .map(Libro::getTitulo)
                    .collect(Collectors.joining(", "));
            System.out.println("Libros: [" + libros + "]");
        });
    }

    private void listarAutoresVivos() {
        System.out.println("Ingrese el año de autor vivo que desea buscar: ");
        int ano = teclado.nextInt();
        teclado.nextLine();
        List<Autor> autoresVivos = autorService.listarAutoresVivoEnAno(ano);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + ano);
        } else {
            autoresVivos.forEach(autor -> {
                System.out.println("-------AUTORES VIVOS-------");
                System.out.println("Autor: " + autor.getNombre());
                System.out.println("Fecha de nacimiento: " + autor.getAnioNacimiento());
                System.out.println("Fecha de fallecimiento: " + autor.getAnioFallecimiento());
                System.out.println("Libros: " + autor.getLibros().size());
            });
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma:
                1) es
                2) en
                3) fr
                4) pt
                """);
        String opcion = teclado.nextLine();
        String idioma=null;
        switch (opcion) {
            case "1" -> idioma = "es";
            case "2" -> idioma = "en";
            case "3" -> idioma = "fr";
            case "4" -> idioma = "pt";
            default -> System.out.println("Opción no válida - Intente de nuevo");
        }
        if (idioma != null) {
            libroService.listarLibrosPorIdioma(idioma)
                    .forEach(libro -> {
                        System.out.println("-------LIBROS SEGÚN IDIOMA-------");
                        System.out.println("Título: " + libro.getTitulo());
                        System.out.println("Autor: " +
                                (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                        System.out.println("Idioma: " + libro.getIdioma());
                        System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
                    });
        }
    }
}
