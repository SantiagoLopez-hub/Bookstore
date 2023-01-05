package com.santiago.bookstore.service;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;
    private final PublisherRepo publisherRepo;

    public ResponseEntity<Iterable<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepo.findAll());
    }

    public ResponseEntity<Book> getBook(Long bookId) {
        Book book = bookRepo.findById(bookId).orElse(null);
        if (book == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

        return ResponseEntity.ok(book);
    }

    public ResponseEntity<String> createBook(Double price,
                                             String title,
                                             String isbn,
                                             Long authorId,
                                             Long publisherId) {
        Author author = authorRepo.findById(authorId).orElse(null);
        Publisher publisher = publisherRepo.findById(publisherId).orElse(null);

        if (author == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");}
        if (publisher == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publisher not found.");}

        Book book = Book.builder()
                .price(price)
                .title(title)
                .isbn(isbn)
                .author(author)
                .publisher(publisher)
                .build();

        bookRepo.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created book with title " + title + ".");
    }

    public ResponseEntity<String> updateBook(Long bookId,
                                             Double price,
                                             String title,
                                             String isbn,
                                             Long authorId,
                                             Long publisherId) {
        try {
            Author author = authorRepo.findById(authorId).orElse(null);
            Book book = bookRepo.findById(bookId).orElse(null);
            Publisher publisher = publisherRepo.findById(publisherId).orElse(null);

            if (author == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");}
            if (book == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");}
            if (publisher == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publisher not found.");}

            book.setPrice(price);
            book.setTitle(title);
            book.setIsbn(isbn);
            book.setAuthor(author);
            book.setPublisher(publisher);
            bookRepo.save(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Book " + bookId + " Updated");
    }

    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        try {
            bookRepo.deleteById(bookId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Book " + bookId + " Deleted");
    }
}
