package com.alura.LiterAlura.repository;

import com.alura.LiterAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNombreIgnoreCase(String nombre);

    @Query("""
        SELECT a FROM Autor a
        WHERE a.anioNacimiento <= :anio
        AND (a.anioFallecimiento IS NULL OR a.anioFallecimiento >= :anio)
    """)
    List<Autor> findAutoresVivosEnAnio(int anio);
}
