package com.alura.Literlalura.repositorio;

import com.alura.Literlalura.modelos.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepositorio extends JpaRepository<Libros, Long> {

    @Query("SELECT l FROM Libros l WHERE l.lenguaje ILIKE %:lenguaje%")
    List<Libros> findByLenguaje(String lenguaje);
}
