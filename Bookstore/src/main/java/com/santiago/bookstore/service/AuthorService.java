package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepo authorRepo;
    private final BookRepo bookRepo;

    public List<Author> getAllAuthors() {
        return authorRepo.findAll();
    }

    public Author getAuthor(Long authorId) {
        return authorRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
    }

    @Transactional
    public Author createAuthor(AuthorRequest authorRequest) {
        Author author = Author.builder()
                .name(authorRequest.getName())
                .build();

        return authorRepo.save(author);
    }

    @Transactional
    public Author updateAuthor(Long authorId, AuthorRequest authorRequest) {
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));

        author.setName(authorRequest.getName());
        return authorRepo.save(author);
    }

    @Transactional
    public void deleteAuthor(Long authorId) {
        if (!authorRepo.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }
        // Cascade, remove books belonging to author
        bookRepo.deleteAll(bookRepo.findByAuthorId(authorId));

        // Remove author
        authorRepo.deleteById(authorId);
    }
}
