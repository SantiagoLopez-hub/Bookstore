package com.santiago.bookstore.route;

import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorRoute {
    private final AuthorService authorService;

    // Get all authors
    @GetMapping
    public ResponseEntity<List<Author>> getAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    // Get specific author by id
    @GetMapping("/{authorId}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(authorService.getAuthor(authorId));
    }

    // Create author
    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(authorRequest));
    }

    // Update author
    @PutMapping("/{authorId}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long authorId, @Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.ok(authorService.updateAuthor(authorId, authorRequest));
    }

    // Delete author
    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.noContent().build();
    }
}
