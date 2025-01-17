package com.alura.Literlalura.main;

import com.alura.Literlalura.modelos.Autor;
import com.alura.Literlalura.modelos.DatosLibros;
import com.alura.Literlalura.modelos.Libros;
import com.alura.Literlalura.repositorio.AutorRepositorio;
import com.alura.Literlalura.repositorio.LibroRepositorio;
import com.alura.Literlalura.servicios.ConvierteDatos;
import com.alura.Literlalura.servicios.RequestAPI;

import java.util.List;
import java.util.Scanner;

public class Principal{
    private RequestAPI requestAPI = new RequestAPI();
    private Scanner scanner = new Scanner(System.in);
    private String urlBase ="https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private LibroRepositorio libroRepositorio;
    private AutorRepositorio autorRepositorio;
    private List<Libros> libros;
    private List<Autor> autor;

    public Principal(LibroRepositorio libroRepositorio, AutorRepositorio autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void showMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ================================================================
                    =        * LiterAlura *  Busqueda de Libros y Autores          =
                    ================================================================
                    \s
                    Selecciona una opcion del menú:\s
                    ================================================================
                    \s
                    1 - Buscar un libro
                    2 - Consultar libros buscados
                    3 - Consultar autores
                    4 - Consultar autores de un año especifico
                    5 - Consultar libros por lenguaje
                    \s
                    0 - Salir   \s
                    ================================================================
                    Opcion: \s
                                        
                                        
                    """;

            try {
                System.out.println(menu);
                opcion = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Por favor, ingresa una opción válida");
            }

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    consultarLibros();
                    break;
                case 3:
                    consultarAutores();
                    break;
                case 4:
                    consultarAutoresPorAño();
                    break;
                case 5:
                    consultarLibrosLenguaje();
                    break;
                case 0:
                    System.out.println("Gracias por usar LITERALURA, nos vemos... C:");
                    break;
                default:
                    System.out.println("Por favor, escoge una opción válida");
            }
        }
    }
    private DatosLibros getDatosLibro() {
        System.out.println("Por favor, ingrese el nombre del libro que desea buscar: ");
        var busqueda = scanner.nextLine().toLowerCase().replace(" ", "%20");
        var json = requestAPI.getData(urlBase + "?search=" + busqueda);


        // Verifica si la respuesta es vacía o nula
        if (json == null || json.isEmpty()) {
            System.out.println("La respuesta de la API está vacía.");
            return null;
        }

        // Intenta deserializar la respuesta JSON
        try {
            DatosLibros datosLibros = convierteDatos.obtenerDatos(json, DatosLibros.class);
            if (datosLibros.resultados().isEmpty()) {
                System.out.println("No se encontraron libros.");
                return null;
            }
            return datosLibros;
        } catch (Exception e) {
            System.out.println("Error al procesar la respuesta de la API: " + e.getMessage());
            return null;
        }
    }


    private void buscarLibro() {
        DatosLibros datosLibros = getDatosLibro();
        if (datosLibros == null) {
            System.out.println("No se encontraron resultados para la búsqueda.");
            return;
        }

        try {
            Libros libro = new Libros(datosLibros.resultados().get(0));
            Autor autor = new Autor(datosLibros.resultados().get(0).autorList().get(0));

            System.out.println("""
                ================================
                =  Resultado del libro buscado =
                ================================
                Titulo: %s
                Autor: %s
                Idioma: %s
                Descargas: %s
                """.formatted(libro.getTitulo(), libro.getAutor(), libro.getLenguaje(), libro.getDescargas()));

            libroRepositorio.save(libro);
            autorRepositorio.save(autor);

} catch (Exception e){
            System.out.println("""
                    
                    =======================================
                    =  No se encontró el libro ingresado  =
                    =======================================
                    
                    """);
        }
    }

    private void consultarLibros() {
        libros = libroRepositorio.findAll();
        libros.stream().forEach(l-> {
            System.out.println("""
                    
                    =======================================
                    =          Datos  del  libro          =
                    =======================================
                                        
                    Titulo: %s
                    Autor: %s
                    Idioma: %s
                    Descargas: %s
                    
                    """.formatted(l.getTitulo(),
                    l.getAutor(),
                    l.getLenguaje(),
                    l.getDescargas().toString()));
        });
    }

    private void consultarAutores() {
        autor = autorRepositorio.findAll();
        autor.stream().forEach(a -> {
            System.out.println("""
                    
                    =======================================
                   =          Datos  del Autor           =
                   =======================================
                   \s
                   Autor: %s
                   Año de Nacimiento: %s
                   Año de Muerte: %s
                    
                    """.formatted(a.getAutor(),
                    a.getNacimiento().toString(),
                    a.getDefuncion().toString()));
        });
    }

    public void consultarAutoresPorAño(){

        System.out.println("Ingrese el año a partir del cual buscar: ");
        var añoBusqueda = scanner.nextInt();
        scanner.nextLine();

        List<Autor> autors = autorRepositorio.autorPorFecha(añoBusqueda);
        autors.forEach(a -> {
            System.out.println("""
                    
                    =======================================
                    =   Autor vivo en el año consultado   =
                    =======================================
                    \s
                    Nombre: %s
                    Fecha de Nacimiento: %s
                    Fecha de Muerte: %s
                    
                    """.formatted(a.getAutor(),a.getNacimiento().toString(),a.getDefuncion().toString()));
        });
    }

    private void consultarLibrosLenguaje() {
        System.out.println("""
                
            ================================================================   \s
            =   Selecciona el lenguaje de los libros que deseas consultar  =
            ================================================================
            \s
            1 - Ingles  - (EN)
            2 - Español  - (ES)
                
            """);

        try {
            var opcion2 = scanner.nextInt();
            scanner.nextLine();

            switch (opcion2) {
                case 1:
                    libros = libroRepositorio.findByLenguaje("en");
                    break;
                case 2:
                    libros = libroRepositorio.findByLenguaje("es");
                    break;

                default:
                    System.out.println("Por favor ingresa una opcion válida: 1 o 2");
            }

            libros.stream().forEach(l -> {
                System.out.println("""
                        
                        ============================================
                        =   Libros escritos en idioma  consultado  =
                        ============================================
                           \s
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Descargas: %s
                        
                        """.formatted(l.getTitulo(),
                        l.getAutor(),
                        l.getLenguaje(),
                        l.getDescargas().toString()));
            });
        } catch (Exception e) {
            System.out.println("Ingresa un valor válido");
        }
    }
}
