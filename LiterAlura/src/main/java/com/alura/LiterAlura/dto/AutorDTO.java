package com.alura.LiterAlura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AutorDTO(
        String name,
        @JsonAlias("birth_year") Integer nacimiento,
        @JsonAlias("death_year") Integer fallecimiento
) {
}
