package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorRoute {
    private final AuthorRepo authorRepo;
    private final AuthorService authorService;

    // Get all Authors
    @GetMapping
    public ResponseEntity<Iterable<Author>> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    // Get specific Author by id
    @GetMapping("/{authorId}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long authorId) {
        return authorService.getAuthor(authorId);
    }

    // Create Author
    @PostMapping()
    public ResponseEntity<String> createAuthor(@RequestParam String name) {
        return authorService.createAuthor(name);
    }

    // Update Author
    @PutMapping("/{authorId}")
    public ResponseEntity<String> updateAuthor(@PathVariable Long authorId,
                                               @RequestParam String name) {
        return authorService.updateAuthor(authorId, name);
    }

    // Delete Author - Removes all books belonging to author
    @DeleteMapping("/{authorId}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long authorId) {
        return authorService.deleteAuthor(authorId);
    }
}
