package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.BookRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @Mock
    private AuthorRepo authorRepo;

    @Mock
    private PublisherRepo publisherRepo;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Author author;
    private Publisher publisher;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        author = Author.builder().id(1L).name("Author Name").build();
        publisher = Publisher.builder().id(1L).name("Publisher Name").build();
        book = Book.builder()
                .id(1L)
                .title("Book Title")
                .price(19.99)
                .isbn("1234567890")
                .author(author)
                .publisher(publisher)
                .build();

        bookRequest = BookRequest.builder()
                .title("Book Title")
                .price(19.99)
                .isbn("1234567890")
                .authorId(1L)
                .publisherId(1L)
                .build();
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        when(bookRepo.findAll()).thenReturn(Arrays.asList(book));

        List<Book> books = bookService.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals("Book Title", books.get(0).getTitle());
        verify(bookRepo, times(1)).findAll();
    }

    @Test
    void getBook_WithValidId_ShouldReturnBook() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBook(1L);

        assertNotNull(foundBook);
        assertEquals("Book Title", foundBook.getTitle());
        verify(bookRepo, times(1)).findById(1L);
    }

    @Test
    void getBook_WithInvalidId_ShouldThrowException() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBook(99L));
        verify(bookRepo, times(1)).findById(99L);
    }

    @Test
    void createBook_WithValidRequest_ShouldReturnCreatedBook() {
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
        when(bookRepo.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(bookRequest);

        assertNotNull(createdBook);
        assertEquals("Book Title", createdBook.getTitle());
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, times(1)).findById(1L);
        verify(bookRepo, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_WithInvalidAuthorId_ShouldThrowException() {
        when(authorRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookRequest));
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, never()).findById(any());
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void createBook_WithInvalidPublisherId_ShouldThrowException() {
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(publisherRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookRequest));
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, times(1)).findById(1L);
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void updateBook_WithValidIdAndRequest_ShouldReturnUpdatedBook() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
        when(bookRepo.save(any(Book.class))).thenReturn(book);

        Book updatedBook = bookService.updateBook(1L, bookRequest);

        assertNotNull(updatedBook);
        assertEquals("Book Title", updatedBook.getTitle());
        verify(bookRepo, times(1)).findById(1L);
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, times(1)).findById(1L);
        verify(bookRepo, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_WithInvalidBookId_ShouldThrowException() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(99L, bookRequest));
        verify(bookRepo, times(1)).findById(99L);
        verify(authorRepo, never()).findById(any());
        verify(publisherRepo, never()).findById(any());
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void updateBook_WithInvalidAuthorId_ShouldThrowException() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(1L, bookRequest));
        verify(bookRepo, times(1)).findById(1L);
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, never()).findById(any());
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void updateBook_WithInvalidPublisherId_ShouldThrowException() {
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(publisherRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(1L, bookRequest));
        verify(bookRepo, times(1)).findById(1L);
        verify(authorRepo, times(1)).findById(1L);
        verify(publisherRepo, times(1)).findById(1L);
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_WithValidId_ShouldDeleteBook() {
        when(bookRepo.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepo, times(1)).existsById(1L);
        verify(bookRepo, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_WithInvalidId_ShouldThrowException() {
        when(bookRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(99L));
        verify(bookRepo, times(1)).existsById(99L);
        verify(bookRepo, never()).deleteById(99L);
    }
}
