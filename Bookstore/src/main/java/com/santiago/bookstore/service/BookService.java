package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.BookRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;
    private final PublisherRepo publisherRepo;

    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookRepo.findAll();
    }

    public Book getBook(Long bookId) {
        log.info("Fetching book with id: {}", bookId);
        return bookRepo.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", bookId);
                    return new ResourceNotFoundException("Book not found with id: " + bookId);
                });
    }

    @Transactional
    public Book createBook(BookRequest bookRequest) {
        log.info("Creating book with title: {}", bookRequest.getTitle());
        Author author = authorRepo.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", bookRequest.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + bookRequest.getAuthorId());
                });
        Publisher publisher = publisherRepo.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> {
                    log.error("Publisher not found with id: {}", bookRequest.getPublisherId());
                    return new ResourceNotFoundException("Publisher not found with id: " + bookRequest.getPublisherId());
                });

        Book book = Book.builder()
                .price(bookRequest.getPrice())
                .title(bookRequest.getTitle())
                .isbn(bookRequest.getIsbn())
                .author(author)
                .publisher(publisher)
                .build();

        Book savedBook = bookRepo.save(book);
        log.info("Book created with id: {}", savedBook.getId());
        return savedBook;
    }

    @Transactional
    public Book updateBook(Long bookId, BookRequest bookRequest) {
        log.info("Updating book with id: {}", bookId);
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", bookId);
                    return new ResourceNotFoundException("Book not found with id: " + bookId);
                });
        Author author = authorRepo.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> {
                    log.error("Author not found with id: {}", bookRequest.getAuthorId());
                    return new ResourceNotFoundException("Author not found with id: " + bookRequest.getAuthorId());
                });
        Publisher publisher = publisherRepo.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> {
                    log.error("Publisher not found with id: {}", bookRequest.getPublisherId());
                    return new ResourceNotFoundException("Publisher not found with id: " + bookRequest.getPublisherId());
                });

        book.setPrice(bookRequest.getPrice());
        book.setTitle(bookRequest.getTitle());
        book.setIsbn(bookRequest.getIsbn());
        book.setAuthor(author);
        book.setPublisher(publisher);

        Book updatedBook = bookRepo.save(book);
        log.info("Book updated with id: {}", updatedBook.getId());
        return updatedBook;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        log.info("Deleting book with id: {}", bookId);
        if (!bookRepo.existsById(bookId)) {
            log.error("Book not found with id: {}", bookId);
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }
        bookRepo.deleteById(bookId);
        log.info("Book deleted with id: {}", bookId);
    }
}
