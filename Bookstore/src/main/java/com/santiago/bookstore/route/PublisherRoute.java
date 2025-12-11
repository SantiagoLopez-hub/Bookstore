package com.santiago.bookstore.route;

import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publishers")
public class PublisherRoute {
    private final PublisherService publisherService;

    // Get all publishers
    @GetMapping
    public ResponseEntity<List<Publisher>> getPublishers() {
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }

    // Get specific publisher by id
    @GetMapping("/{publisherId}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable Long publisherId) {
        return ResponseEntity.ok(publisherService.getPublisher(publisherId));
    }

    // Create publisher
    @PostMapping
    public ResponseEntity<Publisher> createPublisher(@Valid @RequestBody PublisherRequest publisherRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publisherService.createPublisher(publisherRequest));
    }

    // Update publisher
    @PutMapping("/{publisherId}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long publisherId, @Valid @RequestBody PublisherRequest publisherRequest) {
        return ResponseEntity.ok(publisherService.updatePublisher(publisherId, publisherRequest));
    }

    // Delete publisher
    @DeleteMapping("/{publisherId}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long publisherId) {
        publisherService.deletePublisher(publisherId);
        return ResponseEntity.noContent().build();
    }
}
