package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookRoute {
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;

    // Get all books
    @GetMapping
    public ResponseEntity<Iterable<Book>> getBooks() {
        return ResponseEntity.ok(bookRepo.findAll());
    }

    // Get specific book by id
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        Book book = bookRepo.findById(bookId).orElse(null);
        if (book == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

        return ResponseEntity.ok(book);
    }

    // Create book
    @PostMapping()
    public ResponseEntity<String> createBook(@RequestParam Double price,
                                             @RequestParam String title,
                                             @RequestParam String isbn,
                                             @RequestParam Long authorId,
                                             @RequestParam String publisher) {

        Author author = authorRepo.findById(authorId).orElse(null);
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
                                             @RequestParam Long authorId,
                                             @RequestParam String publisher) {
        try {
            Author author = authorRepo.findById(authorId).orElse(null);
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
