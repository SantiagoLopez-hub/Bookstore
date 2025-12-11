package com.santiago.bookstore.service;

import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.exception.ResourceNotFoundException;
import com.santiago.bookstore.model.Publisher;
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
public class PublisherService {
    private final PublisherRepo publisherRepo;
    private final BookRepo bookRepo;

    public List<Publisher> getAllPublishers() {
        log.info("Fetching all publishers");
        return publisherRepo.findAll();
    }

    public Publisher getPublisher(Long publisherId) {
        log.info("Fetching publisher with id: {}", publisherId);
        return publisherRepo.findById(publisherId)
                .orElseThrow(() -> {
                    log.error("Publisher not found with id: {}", publisherId);
                    return new ResourceNotFoundException("Publisher not found with id: " + publisherId);
                });
    }

    @Transactional
    public Publisher createPublisher(PublisherRequest publisherRequest) {
        log.info("Creating publisher with name: {}", publisherRequest.getName());
        Publisher publisher = Publisher.builder()
                .name(publisherRequest.getName())
                .build();

        Publisher savedPublisher = publisherRepo.save(publisher);
        log.info("Publisher created with id: {}", savedPublisher.getId());
        return savedPublisher;
    }

    @Transactional
    public Publisher updatePublisher(Long publisherId, PublisherRequest publisherRequest) {
        log.info("Updating publisher with id: {}", publisherId);
        Publisher publisher = publisherRepo.findById(publisherId)
                .orElseThrow(() -> {
                    log.error("Publisher not found with id: {}", publisherId);
                    return new ResourceNotFoundException("Publisher not found with id: " + publisherId);
                });

        publisher.setName(publisherRequest.getName());
        Publisher updatedPublisher = publisherRepo.save(publisher);
        log.info("Publisher updated with id: {}", updatedPublisher.getId());
        return updatedPublisher;
    }

    @Transactional
    public void deletePublisher(Long publisherId) {
        log.info("Deleting publisher with id: {}", publisherId);
        if (!publisherRepo.existsById(publisherId)) {
            log.error("Publisher not found with id: {}", publisherId);
            throw new ResourceNotFoundException("Publisher not found with id: " + publisherId);
        }
        // Cascade, remove books belonging to author
        log.debug("Deleting related books for publisher id: {}", publisherId);
        bookRepo.deleteAll(bookRepo.findByPublisherId(publisherId));

        // Remove publisher
        publisherRepo.deleteById(publisherId);
        log.info("Publisher deleted with id: {}", publisherId);
    }
}
