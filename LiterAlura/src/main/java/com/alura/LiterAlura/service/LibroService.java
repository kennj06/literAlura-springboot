package com.alura.LiterAlura.service;

import com.alura.LiterAlura.model.Autor;
import com.alura.LiterAlura.model.Libro;
import com.alura.LiterAlura.repository.AutorRepository;
import com.alura.LiterAlura.repository.LibroRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public List<Libro> buscarLibroPorTitulo(String titulo) {
        return libroRepository.findByTituloContainsIgnoreCase(titulo);
    }

    public Libro guardarLibro(String titulo, String idioma, Double descargas, String nombreAutor, Integer nacimiento, Integer fallecimiento) {

        Autor autor = autorRepository.findByNombreIgnoreCase(nombreAutor)
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(nombreAutor);
                    nuevoAutor.setAnioNacimiento(nacimiento);
                    nuevoAutor.setAnioFallecimiento(fallecimiento);
                    return autorRepository.save(nuevoAutor);
                });

        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIdioma(idioma);
        libro.setNumeroDescargas(descargas);
        libro.setAutor(autor);

        return libroRepository.save(libro);
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public List<Libro> listarPorIdioma(String idioma) {

        return libroRepository.findByIdioma(idioma);
    }
}