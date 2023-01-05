package com.santiago.bookstore.service;

import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
    private final AuthorRepo authorRepo;
    private final BookRepo bookRepo;

    public ResponseEntity<Iterable<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorRepo.findAll());
    }

    public ResponseEntity<Author> getAuthor(Long authorId) {
        Author author = authorRepo.findById(authorId).orElse(null);
        if (author == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

        return ResponseEntity.ok(author);
    }

    public ResponseEntity<String> createAuthor(String name) {
        Author author = Author.builder()
                .name(name)
                .build();

        authorRepo.save(author);

        return ResponseEntity.status(HttpStatus.CREATED).body("Created author with name " + name + ".");
    }

    public ResponseEntity<String> updateAuthor(Long authorId, String name) {
        try {
            Author author = authorRepo.findById(authorId).orElse(null);
            if (author == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found.");
            }

            author.setName(name);
            authorRepo.save(author);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok("Author " + authorId + " Updated");
    }

    public ResponseEntity<String> deleteAuthor(Long authorId) {
        try {
            // Cascade, remove books belonging to author
            bookRepo.deleteAll(bookRepo.findByAuthorId(authorId));

            // Remove author
            authorRepo.deleteById(authorId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok("Author " + authorId + " Deleted");
    }
}
