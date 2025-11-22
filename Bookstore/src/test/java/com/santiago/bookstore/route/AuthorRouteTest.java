package com.santiago.bookstore.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.service.AuthorService;
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

@WebMvcTest(AuthorRoute.class)
class AuthorRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Author author;
    private AuthorRequest authorRequest;

    @BeforeEach
    void setUp() {
        author = Author.builder().id(1L).name("Author Name").build();
        authorRequest = AuthorRequest.builder().name("Author Name").build();
    }

    @Test
    void getAuthors_ShouldReturnListOfAuthors() throws Exception {
        when(authorService.getAllAuthors()).thenReturn(Arrays.asList(author));

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Author Name"));
    }

    @Test
    void getAuthor_WithValidId_ShouldReturnAuthor() throws Exception {
        when(authorService.getAuthor(1L)).thenReturn(author);

        mockMvc.perform(get("/authors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Author Name"));
    }

    @Test
    void getAuthor_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(authorService.getAuthor(99L)).thenThrow(new ResourceNotFoundException("Author not found"));

        mockMvc.perform(get("/authors/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAuthor_WithValidRequest_ShouldReturnCreatedAuthor() throws Exception {
        when(authorService.createAuthor(any(AuthorRequest.class))).thenReturn(author);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Author Name"));
    }

    @Test
    void createAuthor_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        authorRequest.setName(null);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAuthor_WithValidRequest_ShouldReturnUpdatedAuthor() throws Exception {
        when(authorService.updateAuthor(eq(1L), any(AuthorRequest.class))).thenReturn(author);

        mockMvc.perform(put("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Author Name"));
    }

    @Test
    void updateAuthor_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(authorService.updateAuthor(eq(99L), any(AuthorRequest.class))).thenThrow(new ResourceNotFoundException("Author not found"));

        mockMvc.perform(put("/authors/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/authors/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Author not found")).when(authorService).deleteAuthor(99L);

        mockMvc.perform(delete("/authors/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}
