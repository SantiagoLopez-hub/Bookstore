package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorRoute {
    private final AuthorRepo authorRepo;

    // Get all Authors
    @GetMapping
    public ResponseEntity<List<Author>> getAuthors() {
        return ResponseEntity.ok((List<Author>) authorRepo.findAll());
    }

    // Get specific Author by id
    @GetMapping("/{authorId}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(authorRepo.findById(authorId).orElse(null));
    }

    // Create Author
    @PostMapping()
    public ResponseEntity<String> createAuthor(@RequestParam String name) {
        Author author = Author.builder()
                .name(name)
                .build();

        authorRepo.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created author with name " + name + ".");
    }

    // Update Author
    @PutMapping("/{authorId}")
    public ResponseEntity<String> updateAuthor(@PathVariable Long authorId,
                                               @RequestParam String name) {
        try {
            Author author = authorRepo.findById(authorId).orElse(null);
            assert author != null;

            author.setName(name);
            authorRepo.save(author);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Author " + authorId + " Updated");
    }

    // Delete Author
    @DeleteMapping("/{authorId}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long authorId) {
        try {
            authorRepo.deleteById(authorId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Author " + authorId + " Deleted");
    }
}
