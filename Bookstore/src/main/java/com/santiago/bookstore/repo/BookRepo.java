package com.santiago.bookstore.repo;

import com.santiago.bookstore.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends CrudRepository<Book, Long> {
    List<Book> findByAuthorId(Long authorId);
}
