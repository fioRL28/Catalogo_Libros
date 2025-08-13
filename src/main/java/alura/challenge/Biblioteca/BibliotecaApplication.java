package alura.challenge.Biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import alura.challenge.Biblioteca.principal.Principal;

@SpringBootApplication
public class BibliotecaApplication implements CommandLineRunner {


    @Autowired
    private final Principal principal;

    public BibliotecaApplication(Principal principal) {
        this.principal = principal;
    }

    @Override
    public void run(String... args) throws Exception {

        principal.muestraMenu();
    }
    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }

}
