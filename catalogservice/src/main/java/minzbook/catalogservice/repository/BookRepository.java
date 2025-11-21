package minzbook.catalogservice.repository;

import minzbook.catalogservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByActivoTrue();

    List<Book> findByCategoriaAndActivoTrue(String categoria);
}
