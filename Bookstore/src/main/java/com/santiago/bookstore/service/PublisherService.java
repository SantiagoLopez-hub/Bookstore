package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepo publisherRepo;
    private final BookRepo bookRepo;

    public List<Publisher> getAllPublishers() {
        return publisherRepo.findAll();
    }

    public Publisher getPublisher(Long publisherId) {
        return publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + publisherId));
    }

    @Transactional
    public Publisher createPublisher(PublisherRequest publisherRequest) {
        Publisher publisher = Publisher.builder()
                .name(publisherRequest.getName())
                .build();

        return publisherRepo.save(publisher);
    }

    @Transactional
    public Publisher updatePublisher(Long publisherId, PublisherRequest publisherRequest) {
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id: " + publisherId));

        publisher.setName(publisherRequest.getName());
        return publisherRepo.save(publisher);
    }

    @Transactional
    public void deletePublisher(Long publisherId) {
        if (!publisherRepo.existsById(publisherId)) {
            throw new ResourceNotFoundException("Publisher not found with id: " + publisherId);
        }
        // Cascade, remove books belonging to author
        bookRepo.deleteAll(bookRepo.findByPublisherId(publisherId));

        // Remove publisher
        publisherRepo.deleteById(publisherId);
    }
}
