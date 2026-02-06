package com.alura.LiterAlura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultadoDTO(
        Integer count,
        String next,           // 🔹 Agregado para paginación
        String previous,       // 🔹 Opcional, si quieres manejar "anterior"
        List<LibroDTO> results
) {
}
