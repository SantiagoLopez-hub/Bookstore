package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookRoute {
    private final BookRepo bookRepo;

    // Get all books
    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        return ResponseEntity.ok((List<Book>) bookRepo.findAll());
    }

    // Get specific book by id
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookRepo.findById(bookId).orElse(null));
    }

    // Create book
    @PostMapping()
    public ResponseEntity<String> createBook(@RequestParam Double price,
                                             @RequestParam String title,
                                             @RequestParam String isbn,
                                             @RequestParam String author,
                                             @RequestParam String publisher) {
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

    // Update book
    @PutMapping("/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable Long bookId,
                                             @RequestParam Double price,
                                             @RequestParam String title,
                                             @RequestParam String isbn,
                                             @RequestParam String author,
                                             @RequestParam String publisher) {
        try {
            Book book = bookRepo.findById(bookId).orElse(null);
            assert book != null;

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

    // Delete book
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        try {
            bookRepo.deleteById(bookId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Book " + bookId + " Deleted");
    }
}
