package com.alura.Literlalura;

import com.alura.Literlalura.main.Principal;
import com.alura.Literlalura.repositorio.AutorRepositorio;
import com.alura.Literlalura.repositorio.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterlaluraApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LiterlaluraApplication.class, args);
	}

	@Autowired
	private LibroRepositorio repositorio;

	@Autowired
	private AutorRepositorio autorRepositorio;

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorio, autorRepositorio);
		principal.showMenu();
	}
}
