package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.PublisherRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publishers")
public class PublisherRoute {
    private final PublisherRepo publisherRepo;

    // Get all Publishers
    @GetMapping
    public ResponseEntity<Iterable<Publisher>> getPublishers() {
        return ResponseEntity.ok(publisherRepo.findAll());
    }

    // Get specific Publisher by id
    @GetMapping("/{publisherId}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable Long publisherId) {
        Publisher publisher = publisherRepo.findById(publisherId).orElse(null);
        if (publisher == null) {return ResponseEntity.status(HttpStatus.NOT_FOUND).build();}

        return ResponseEntity.ok(publisher);
    }

    // Create Publisher
    @PostMapping()
    public ResponseEntity<String> createPublisher(@RequestParam String name) {
        Publisher publisher = Publisher.builder()
                .name(name)
                .build();

        publisherRepo.save(publisher);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created Publisher with name " + name + ".");
    }

    // Update Publisher
    @PutMapping("/{publisherId}")
    public ResponseEntity<String> updatePublisher(@PathVariable Long publisherId,
                                               @RequestParam String name) {
        try {
            Publisher publisher = publisherRepo.findById(publisherId).orElse(null);
            assert publisher != null;

            publisher.setName(name);
            publisherRepo.save(publisher);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Publisher " + publisherId + " Updated");
    }

    // Delete Publisher
    @DeleteMapping("/{PublisherId}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long publisherId) {
        try {
            publisherRepo.deleteById(publisherId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body("Publisher " + publisherId + " Deleted");
    }
}
