package com.santiago.bookstore.route;

import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.service.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publishers")
public class PublisherRoute {
    private final PublisherService publisherService;

    // Get all Publishers
    @GetMapping
    public ResponseEntity<Iterable<Publisher>> getPublishers() {
        return publisherService.getAllPublishers();
    }

    // Get specific Publisher by id
    @GetMapping("/{publisherId}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable Long publisherId) {
        return publisherService.getPublisher(publisherId);
    }

    // Create Publisher
    @PostMapping
    public ResponseEntity<String> createPublisher(@RequestParam String name) {
        return publisherService.createPublisher(name);
    }

    // Update Publisher
    @PutMapping("/{publisherId}")
    public ResponseEntity<String> updatePublisher(@PathVariable Long publisherId, @RequestParam String name) {
        return publisherService.updatePublisher(publisherId, name);
    }

    // Delete Publisher
    @DeleteMapping("/{publisherId}")
    public ResponseEntity<String> deletePublisher(@PathVariable Long publisherId) {
        return publisherService.deletePublisher(publisherId);
    }
}
