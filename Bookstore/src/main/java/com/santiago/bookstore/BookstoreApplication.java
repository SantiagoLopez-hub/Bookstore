package com.santiago.bookstore;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

     @Bean
     public CommandLineRunner runner(BookRepo bookRepo, AuthorRepo authorRepo) {
         return args -> {
             authorRepo.save(Author.builder().id(1L).name("Rodrigo").build());

             bookRepo.save(Book.builder().id(1L).title("The Lord of the Rings")
                             .author(authorRepo.findById(1L).get().getName()).build());
             bookRepo.save(Book.builder().id(2L).title("The Hobbit").build());
             bookRepo.save(Book.builder().id(3L).title("The Silmarillion").build());
         };
     }
}
