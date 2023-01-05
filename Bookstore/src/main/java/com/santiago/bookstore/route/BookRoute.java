package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookRoute {
    private final BookService bookService;

    // Get all books
    @GetMapping
    public ResponseEntity<Iterable<Book>> getBooks() {
        return bookService.getAllBooks();
    }

    // Get specific book by id
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        return bookService.getBook(bookId);
    }

    // Create book
    @PostMapping
    public ResponseEntity<String> createBook(@RequestParam Double price,
                                             @RequestParam String title,
                                             @RequestParam String isbn,
                                             @RequestParam Long authorId,
                                             @RequestParam Long publisherId) {
        return bookService.createBook(price, title, isbn, authorId, publisherId);
    }

    // Update book
    @PutMapping("/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable Long bookId,
                                             @RequestParam Double price,
                                             @RequestParam String title,
                                             @RequestParam String isbn,
                                             @RequestParam Long authorId,
                                             @RequestParam Long publisherId) {
        return bookService.updateBook(bookId, price, title, isbn, authorId, publisherId);
    }

    // Delete book
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        return bookService.deleteBook(bookId);
    }
}
