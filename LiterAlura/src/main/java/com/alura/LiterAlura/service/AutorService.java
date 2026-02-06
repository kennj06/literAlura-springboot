package com.alura.LiterAlura.service;

import com.alura.LiterAlura.model.Autor;
import com.alura.LiterAlura.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Autor obtenerOCrearAutor(String nombre, Integer nacimiento, Integer fallecimiento) {
        return autorRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> autorRepository.save(
                        new Autor(nombre, nacimiento, fallecimiento)
                ));
    }

    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    public List<Autor> listarAutoresVivos(int anio) {
        return autorRepository.findAutoresVivosEnAnio(anio);
    }
}
