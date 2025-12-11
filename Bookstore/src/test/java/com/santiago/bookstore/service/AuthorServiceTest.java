package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepo authorRepo;

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorRequest authorRequest;

    @BeforeEach
    void setUp() {
        author = Author.builder().id(1L).name("Author Name").build();
        authorRequest = AuthorRequest.builder().name("Author Name").build();
    }

    @Test
    void getAllAuthors_ShouldReturnListOfAuthors() {
        when(authorRepo.findAll()).thenReturn(Arrays.asList(author));

        List<Author> authors = authorService.getAllAuthors();

        assertNotNull(authors);
        assertEquals(1, authors.size());
        assertEquals("Author Name", authors.get(0).getName());
        verify(authorRepo, times(1)).findAll();
    }

    @Test
    void getAuthor_WithValidId_ShouldReturnAuthor() {
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));

        Author foundAuthor = authorService.getAuthor(1L);

        assertNotNull(foundAuthor);
        assertEquals("Author Name", foundAuthor.getName());
        verify(authorRepo, times(1)).findById(1L);
    }

    @Test
    void getAuthor_WithInvalidId_ShouldThrowException() {
        when(authorRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthor(99L));
        verify(authorRepo, times(1)).findById(99L);
    }

    @Test
    void createAuthor_WithValidRequest_ShouldReturnCreatedAuthor() {
        when(authorRepo.save(any(Author.class))).thenReturn(author);

        Author createdAuthor = authorService.createAuthor(authorRequest);

        assertNotNull(createdAuthor);
        assertEquals("Author Name", createdAuthor.getName());
        verify(authorRepo, times(1)).save(any(Author.class));
    }

    @Test
    void updateAuthor_WithValidIdAndRequest_ShouldReturnUpdatedAuthor() {
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepo.save(any(Author.class))).thenReturn(author);

        Author updatedAuthor = authorService.updateAuthor(1L, authorRequest);

        assertNotNull(updatedAuthor);
        assertEquals("Author Name", updatedAuthor.getName());
        verify(authorRepo, times(1)).findById(1L);
        verify(authorRepo, times(1)).save(any(Author.class));
    }

    @Test
    void updateAuthor_WithInvalidId_ShouldThrowException() {
        when(authorRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.updateAuthor(99L, authorRequest));
        verify(authorRepo, times(1)).findById(99L);
        verify(authorRepo, never()).save(any(Author.class));
    }

    @Test
    void deleteAuthor_WithValidId_ShouldDeleteAuthorAndRelatedBooks() {
        when(authorRepo.existsById(1L)).thenReturn(true);
        when(bookRepo.findByAuthorId(1L)).thenReturn(Collections.emptyList());

        authorService.deleteAuthor(1L);

        verify(authorRepo, times(1)).existsById(1L);
        verify(bookRepo, times(1)).deleteAll(Collections.emptyList());
        verify(authorRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteAuthor_WithInvalidId_ShouldThrowException() {
        when(authorRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(99L));
        verify(authorRepo, times(1)).existsById(99L);
        verify(bookRepo, never()).deleteAll(any());
        verify(authorRepo, never()).deleteById(99L);
    }
}
