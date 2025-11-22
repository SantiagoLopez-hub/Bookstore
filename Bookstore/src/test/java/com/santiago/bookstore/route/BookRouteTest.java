package com.santiago.bookstore.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santiago.bookstore.dto.BookRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRoute.class)
class BookRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        Author author = Author.builder().id(1L).name("Author Name").build();
        Publisher publisher = Publisher.builder().id(1L).name("Publisher Name").build();
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
    void getBooks_ShouldReturnListOfBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book Title"));
    }

    @Test
    void getBook_WithValidId_ShouldReturnBook() throws Exception {
        when(bookService.getBook(1L)).thenReturn(book);

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void getBook_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(bookService.getBook(99L)).thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(get("/books/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_WithValidRequest_ShouldReturnCreatedBook() throws Exception {
        when(bookService.createBook(any(BookRequest.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void createBook_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        bookRequest.setTitle(null); // Invalid request

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_WithValidRequest_ShouldReturnUpdatedBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequest.class))).thenReturn(book);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void updateBook_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(bookService.updateBook(eq(99L), any(BookRequest.class))).thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(put("/books/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteBook_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Book not found")).when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/books/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}
