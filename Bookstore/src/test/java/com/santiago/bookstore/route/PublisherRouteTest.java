package com.santiago.bookstore.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.service.PublisherService;
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

@WebMvcTest(PublisherRoute.class)
class PublisherRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublisherService publisherService;

    @Autowired
    private ObjectMapper objectMapper;

    private Publisher publisher;
    private PublisherRequest publisherRequest;

    @BeforeEach
    void setUp() {
        publisher = Publisher.builder().id(1L).name("Publisher Name").build();
        publisherRequest = PublisherRequest.builder().name("Publisher Name").build();
    }

    @Test
    void getPublishers_ShouldReturnListOfPublishers() throws Exception {
        when(publisherService.getAllPublishers()).thenReturn(Arrays.asList(publisher));

        mockMvc.perform(get("/publishers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Publisher Name"));
    }

    @Test
    void getPublisher_WithValidId_ShouldReturnPublisher() throws Exception {
        when(publisherService.getPublisher(1L)).thenReturn(publisher);

        mockMvc.perform(get("/publishers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Publisher Name"));
    }

    @Test
    void getPublisher_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(publisherService.getPublisher(99L)).thenThrow(new ResourceNotFoundException("Publisher not found"));

        mockMvc.perform(get("/publishers/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPublisher_WithValidRequest_ShouldReturnCreatedPublisher() throws Exception {
        when(publisherService.createPublisher(any(PublisherRequest.class))).thenReturn(publisher);

        mockMvc.perform(post("/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisherRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Publisher Name"));
    }

    @Test
    void createPublisher_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        publisherRequest.setName(null);

        mockMvc.perform(post("/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisherRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePublisher_WithValidRequest_ShouldReturnUpdatedPublisher() throws Exception {
        when(publisherService.updatePublisher(eq(1L), any(PublisherRequest.class))).thenReturn(publisher);

        mockMvc.perform(put("/publishers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisherRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Publisher Name"));
    }

    @Test
    void updatePublisher_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(publisherService.updatePublisher(eq(99L), any(PublisherRequest.class))).thenThrow(new ResourceNotFoundException("Publisher not found"));

        mockMvc.perform(put("/publishers/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisherRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePublisher_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/publishers/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePublisher_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Publisher not found")).when(publisherService).deletePublisher(99L);

        mockMvc.perform(delete("/publishers/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}
