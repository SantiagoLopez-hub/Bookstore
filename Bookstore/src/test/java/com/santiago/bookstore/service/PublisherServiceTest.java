package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
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
class PublisherServiceTest {

    @Mock
    private PublisherRepo publisherRepo;

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private PublisherService publisherService;

    private Publisher publisher;
    private PublisherRequest publisherRequest;

    @BeforeEach
    void setUp() {
        publisher = Publisher.builder().id(1L).name("Publisher Name").build();
        publisherRequest = PublisherRequest.builder().name("Publisher Name").build();
    }

    @Test
    void getAllPublishers_ShouldReturnListOfPublishers() {
        when(publisherRepo.findAll()).thenReturn(Arrays.asList(publisher));

        List<Publisher> publishers = publisherService.getAllPublishers();

        assertNotNull(publishers);
        assertEquals(1, publishers.size());
        assertEquals("Publisher Name", publishers.get(0).getName());
        verify(publisherRepo, times(1)).findAll();
    }

    @Test
    void getPublisher_WithValidId_ShouldReturnPublisher() {
        when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));

        Publisher foundPublisher = publisherService.getPublisher(1L);

        assertNotNull(foundPublisher);
        assertEquals("Publisher Name", foundPublisher.getName());
        verify(publisherRepo, times(1)).findById(1L);
    }

    @Test
    void getPublisher_WithInvalidId_ShouldThrowException() {
        when(publisherRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.getPublisher(99L));
        verify(publisherRepo, times(1)).findById(99L);
    }

    @Test
    void createPublisher_WithValidRequest_ShouldReturnCreatedPublisher() {
        when(publisherRepo.save(any(Publisher.class))).thenReturn(publisher);

        Publisher createdPublisher = publisherService.createPublisher(publisherRequest);

        assertNotNull(createdPublisher);
        assertEquals("Publisher Name", createdPublisher.getName());
        verify(publisherRepo, times(1)).save(any(Publisher.class));
    }

    @Test
    void updatePublisher_WithValidIdAndRequest_ShouldReturnUpdatedPublisher() {
        when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
        when(publisherRepo.save(any(Publisher.class))).thenReturn(publisher);

        Publisher updatedPublisher = publisherService.updatePublisher(1L, publisherRequest);

        assertNotNull(updatedPublisher);
        assertEquals("Publisher Name", updatedPublisher.getName());
        verify(publisherRepo, times(1)).findById(1L);
        verify(publisherRepo, times(1)).save(any(Publisher.class));
    }

    @Test
    void updatePublisher_WithInvalidId_ShouldThrowException() {
        when(publisherRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.updatePublisher(99L, publisherRequest));
        verify(publisherRepo, times(1)).findById(99L);
        verify(publisherRepo, never()).save(any(Publisher.class));
    }

    @Test
    void deletePublisher_WithValidId_ShouldDeletePublisherAndRelatedBooks() {
        when(publisherRepo.existsById(1L)).thenReturn(true);
        when(bookRepo.findByPublisherId(1L)).thenReturn(Collections.emptyList());

        publisherService.deletePublisher(1L);

        verify(publisherRepo, times(1)).existsById(1L);
        verify(bookRepo, times(1)).deleteAll(Collections.emptyList());
        verify(publisherRepo, times(1)).deleteById(1L);
    }

    @Test
    void deletePublisher_WithInvalidId_ShouldThrowException() {
        when(publisherRepo.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> publisherService.deletePublisher(99L));
        verify(publisherRepo, times(1)).existsById(99L);
        verify(bookRepo, never()).deleteAll(any());
        verify(publisherRepo, never()).deleteById(99L);
    }
}
