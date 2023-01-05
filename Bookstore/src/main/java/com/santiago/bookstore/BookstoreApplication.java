package com.santiago.bookstore;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
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
    public CommandLineRunner runner(BookRepo bookRepo, AuthorRepo authorRepo, PublisherRepo publisherRepo) {
        return args -> {
            authorRepo.save(Author.builder().name("Rodrigo").build());
            authorRepo.save(Author.builder().name("William").build());
            authorRepo.save(Author.builder().name("Zoey").build());

            publisherRepo.save(Publisher.builder().name("Penguin").build());

            bookRepo.save(
                    Book.builder()
                            .title("The Lord of the Rings")
                            .author(authorRepo.findById(1L).get())
                            .publisher(publisherRepo.findById(1L).get())
                            .build());

            bookRepo.save(
                    Book.builder()
                            .title("The Hobbit")
                            .author(authorRepo.findById(1L).get())
                            .build());

            bookRepo.save(
                    Book.builder()
                            .title("The Silmarillion")
                            .build());
        };
    }
}
