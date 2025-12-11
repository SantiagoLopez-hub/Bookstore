package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {
    private final AuthorRepo authorRepo;
    private final BookRepo bookRepo;

    public List<Author> getAllAuthors() {
        log.info("Fetching all authors");
        return authorRepo.findAll();
    }

    public Author getAuthor(Long authorId) {
        log.info("Fetching author with id: {}", authorId);
        return authorRepo.findById(authorId)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", authorId);
                    return new ResourceNotFoundException("Author not found with id: " + authorId);
                });
    }

    @Transactional
    public Author createAuthor(AuthorRequest authorRequest) {
        log.info("Creating author with name: {}", authorRequest.getName());
        Author author = Author.builder()
                .name(authorRequest.getName())
                .build();

        Author savedAuthor = authorRepo.save(author);
        log.info("Author created with id: {}", savedAuthor.getId());
        return savedAuthor;
    }

    @Transactional
    public Author updateAuthor(Long authorId, AuthorRequest authorRequest) {
        log.info("Updating author with id: {}", authorId);
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", authorId);
                    return new ResourceNotFoundException("Author not found with id: " + authorId);
                });

        author.setName(authorRequest.getName());
        Author updatedAuthor = authorRepo.save(author);
        log.info("Author updated with id: {}", updatedAuthor.getId());
        return updatedAuthor;
    }

    @Transactional
    public void deleteAuthor(Long authorId) {
        log.info("Deleting author with id: {}", authorId);
        if (!authorRepo.existsById(authorId)) {
            log.error("Author not found with id: {}", authorId);
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }
        // Cascade, remove books belonging to author
        log.debug("Deleting related books for author id: {}", authorId);
        bookRepo.deleteAll(bookRepo.findByAuthorId(authorId));

        // Remove author
        authorRepo.deleteById(authorId);
        log.info("Author deleted with id: {}", authorId);
    }
}
