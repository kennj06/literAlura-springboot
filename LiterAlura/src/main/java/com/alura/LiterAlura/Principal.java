package com.alura.LiterAlura;

import com.alura.LiterAlura.dto.AutorDTO;
import com.alura.LiterAlura.dto.LibroDTO;
import com.alura.LiterAlura.dto.ResultadoDTO;
import com.alura.LiterAlura.model.Libro;
import com.alura.LiterAlura.service.AutorService;
import com.alura.LiterAlura.service.ConsumoAPI;
import com.alura.LiterAlura.service.ConvierteDatos;
import com.alura.LiterAlura.service.LibroService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal implements CommandLineRunner {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final LibroService libroService;
    private final AutorService autorService;

    public Principal(LibroService libroService, AutorService autorService) {
        this.libroService = libroService;
        this.autorService = autorService;
    }

    @Override
    public void run(String... args) {
        mostrarMenu();
    }

    private void mostrarMenu() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("""
                    =========================
                    📚 MENÚ LITERALURA
                    =========================
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    =========================
                    """);

            System.out.print("Elige una opción: ");
            String entrada = scanner.nextLine();

            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, ingresa un número válido.");
                continue;
            }

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("👋 Saliendo de la aplicación...");
                default -> System.out.println("❌ Opción inválida. Intenta de nuevo.");
            }
        }
    }


    private void buscarLibroPorTitulo() {
        System.out.print("🔎 Escribe el título del libro: ");
        String titulo = scanner.nextLine();


        List<Libro> librosEncontrados = libroService.buscarLibroPorTitulo(titulo);

        if (!librosEncontrados.isEmpty()) {
            System.out.println("\n⚠️ El libro ya está registrado en la base de datos local.");
            imprimirLibro(librosEncontrados.get(0));
            return;
        }


        String url = "https://gutendex.com/books/?search=" + titulo.replace(" ", "+");
        var json = consumoAPI.obtenerDatos(url);
        ResultadoDTO resultado = conversor.obtenerDatos(json, ResultadoDTO.class);

        if (resultado == null || resultado.results().isEmpty()) {
            System.out.println("❌ No se encontraron libros con ese título en la API.");
            return;
        }


        LibroDTO libroDTO = resultado.results().get(0);

        if (libroDTO.authors().isEmpty()) {
            System.out.println("❌ El libro encontrado no tiene autor registrado.");
            return;
        }

        AutorDTO autorDTO = libroDTO.authors().get(0);


        Libro libroGuardado = libroService.guardarLibro(
                libroDTO.title(),
                libroDTO.languages().get(0),
                libroDTO.numeroDescargas(),
                autorDTO.name(),
                autorDTO.nacimiento(),
                autorDTO.fallecimiento()
        );

        System.out.println("\n✅ Libro registrado con éxito:");
        imprimirLibro(libroGuardado);
    }

    private void listarLibrosRegistrados() {
        var libros = libroService.listarLibros();
        if (libros.isEmpty()) {
            System.out.println("📭 No hay libros registrados.");
        } else {
            libros.forEach(this::imprimirLibro);
        }
    }

    private void listarAutoresRegistrados() {
        var autores = autorService.listarAutores();
        if (autores.isEmpty()) {
            System.out.println("📭 No hay autores registrados.");
        } else {
            autores.forEach(autor ->
                    System.out.println("""
                        Autor: %s
                        Nacimiento: %s
                        Fallecimiento: %s
                        Libros: %s
                        -----------------
                        """.formatted(
                            autor.getNombre(),
                            autor.getAnioNacimiento(),
                            autor.getAnioFallecimiento(),
                            autor.getLibros().stream()
                                    .map(Libro::getTitulo)
                                    .toList()
                    ))
            );
        }
    }

    private void listarAutoresVivos() {
        System.out.print("📅 Ingresa el año: ");
        try {
            int anio = Integer.parseInt(scanner.nextLine());
            var autoresVivos = autorService.listarAutoresVivos(anio);

            if (autoresVivos.isEmpty()) {
                System.out.println("❌ No hay autores vivos registrados en el año " + anio);
                return;
            }

            autoresVivos.forEach(autor ->
                    System.out.println("""
                        Autor: %s
                        Nacimiento: %s
                        Fallecimiento: %s
                        -----------------
                        """.formatted(
                            autor.getNombre(),
                            autor.getAnioNacimiento(),
                            autor.getAnioFallecimiento()
                    ))
            );
        } catch (NumberFormatException e) {
            System.out.println("❌ Año inválido.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.print("🌍 Ingresa el idioma (en, es, fr, pt): ");
        String idioma = scanner.nextLine().trim().toLowerCase();

        var libros = libroService.listarPorIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros en ese idioma.");
            return;
        }

        System.out.println("\n📚 Libros en '" + idioma + "':");
        libros.forEach(libro -> System.out.println("📘 " + libro.getTitulo()));
    }

    private void imprimirLibro(Libro libro) {
        System.out.println("""
                ----- LIBRO -----
                Título: %s
                Autor: %s
                Idioma: %s
                Descargas: %.1f
                -----------------
                """.formatted(
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getIdioma(),
                libro.getNumeroDescargas()
        ));
    }
}