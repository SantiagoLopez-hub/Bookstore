package com.santiago.bookstore.service;

import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PublisherService {
    private final PublisherRepo publisherRepo;
    private final BookRepo bookRepo;

    public ResponseEntity<Iterable<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherRepo.findAll());
    }

    public ResponseEntity<Publisher> getPublisher(Long publisherId) {
        Publisher publisher = publisherRepo.findById(publisherId).orElse(null);
        if (publisher == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

        return ResponseEntity.ok(publisher);
    }

    public ResponseEntity<String> createPublisher(String name) {
        Publisher publisher = Publisher.builder()
                .name(name)
                .build();

        publisherRepo.save(publisher);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created Publisher with name " + name + ".");
    }

    public ResponseEntity<String> updatePublisher(Long publisherId, String name) {
        try {
            Publisher publisher = publisherRepo.findById(publisherId).orElse(null);
            if (publisher == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publisher not found.");}

            publisher.setName(name);
            publisherRepo.save(publisher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Publisher " + publisherId + " Updated");
    }

    public ResponseEntity<String> deletePublisher(Long publisherId) {
        try {
            // Cascade, remove books belonging to author
            bookRepo.deleteAll(bookRepo.findByPublisherId(publisherId));

            // Remove publisher
            publisherRepo.deleteById(publisherId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Publisher " + publisherId + " Deleted");
    }
}
