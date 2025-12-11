package com.santiago.bookstore.cucumber;

import com.santiago.bookstore.dto.AuthorRequest;
import com.santiago.bookstore.dto.BookRequest;
import com.santiago.bookstore.dto.PublisherRequest;
import com.santiago.bookstore.model.Author;
import com.santiago.bookstore.model.Book;
import com.santiago.bookstore.model.Publisher;
import com.santiago.bookstore.repo.AuthorRepo;
import com.santiago.bookstore.repo.BookRepo;
import com.santiago.bookstore.repo.PublisherRepo;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class BookStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private AuthorRepo authorRepo;

    @Autowired
    private PublisherRepo publisherRepo;

    private Author lastCreatedAuthor;
    private Publisher lastCreatedPublisher;
    private Book lastCreatedBook;
    private ResponseEntity<Book> lastResponse;
    private ResponseEntity<Void> lastDeleteResponse;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/books";
    }

    private String getAuthorsUrl() {
        return "http://localhost:" + port + "/authors";
    }

    private String getPublishersUrl() {
        return "http://localhost:" + port + "/publishers";
    }

    @Given("the database is initialized")
    public void theDatabaseIsInitialized() {
        bookRepo.deleteAll();
        authorRepo.deleteAll();
        publisherRepo.deleteAll();
    }

    @When("I create a new author with name {string}")
    public void iCreateANewAuthorWithName(String name) {
        AuthorRequest request = AuthorRequest.builder().name(name).build();
        ResponseEntity<Author> response = restTemplate.postForEntity(getAuthorsUrl(), request, Author.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        lastCreatedAuthor = response.getBody();
    }

    @When("I create a new publisher with name {string}")
    public void iCreateANewPublisherWithName(String name) {
        PublisherRequest request = PublisherRequest.builder().name(name).build();
        ResponseEntity<Publisher> response = restTemplate.postForEntity(getPublishersUrl(), request, Publisher.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        lastCreatedPublisher = response.getBody();
    }

    @When("I create a new book with title {string}, price {double}, isbn {string}, author {string}, and publisher {string}")
    public void iCreateANewBook(String title, Double price, String isbn, String authorName, String publisherName) {
        // Assuming author and publisher are already created and stored in lastCreatedAuthor/Publisher
        // Ideally we would look them up by name, but for this test flow relying on previous steps is okay
        // OR we can create them if null.

        if (lastCreatedAuthor == null || !lastCreatedAuthor.getName().equals(authorName)) {
             iCreateANewAuthorWithName(authorName);
        }
        if (lastCreatedPublisher == null || !lastCreatedPublisher.getName().equals(publisherName)) {
             iCreateANewPublisherWithName(publisherName);
        }

        BookRequest request = BookRequest.builder()
                .title(title)
                .price(price)
                .isbn(isbn)
                .authorId(lastCreatedAuthor.getId())
                .publisherId(lastCreatedPublisher.getId())
                .build();

        lastResponse = restTemplate.postForEntity(getBaseUrl(), request, Book.class);
        lastCreatedBook = lastResponse.getBody();
    }

    @Then("the book should be created with title {string}")
    public void theBookShouldBeCreatedWithTitle(String title) {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertNotNull(lastCreatedBook);
        assertEquals(title, lastCreatedBook.getTitle());
    }

    @Given("a book exists with title {string}")
    public void aBookExistsWithTitle(String title) {
        iCreateANewBook(title, 10.0, "123", "Default Author", "Default Publisher");
    }

    @When("I request the book with title {string}")
    public void iRequestTheBookWithTitle(String title) {
        // Since we don't have a search by title endpoint exposed, we use the ID from the setup step
        // In a real scenario, we might query the DB to get the ID for the title

        // Find ID by title from DB for accuracy
        Book book = bookRepo.findAll().stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in DB setup"));

        lastResponse = restTemplate.getForEntity(getBaseUrl() + "/" + book.getId(), Book.class);
    }

    @Then("the response should contain title {string}")
    public void theResponseShouldContainTitle(String title) {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        assertNotNull(lastResponse.getBody());
        assertEquals(title, lastResponse.getBody().getTitle());
    }

    @When("I update the book {string} with title {string}")
    public void iUpdateTheBookWithTitle(String oldTitle, String newTitle) {
        Book book = bookRepo.findAll().stream()
                .filter(b -> b.getTitle().equals(oldTitle))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in DB setup"));

        BookRequest request = BookRequest.builder()
                .title(newTitle)
                .price(book.getPrice())
                .isbn(book.getIsbn())
                .authorId(book.getAuthor().getId())
                .publisherId(book.getPublisher().getId())
                .build();

        // Use exchange for PUT
        HttpEntity<BookRequest> entity = new HttpEntity<>(request);
        lastResponse = restTemplate.exchange(getBaseUrl() + "/" + book.getId(), HttpMethod.PUT, entity, Book.class);
    }

    @Then("the book should have title {string}")
    public void theBookShouldHaveTitle(String title) {
         assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
         assertNotNull(lastResponse.getBody());
         assertEquals(title, lastResponse.getBody().getTitle());
    }

    @When("I delete the book {string}")
    public void iDeleteTheBook(String title) {
        Book book = bookRepo.findAll().stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in DB setup"));

        lastDeleteResponse = restTemplate.exchange(getBaseUrl() + "/" + book.getId(), HttpMethod.DELETE, null, Void.class);
        // Store ID to verify deletion? Or just check response status
    }

    @Then("the book {string} should no longer exist")
    public void theBookShouldNoLongerExist(String title) {
        assertEquals(HttpStatus.NO_CONTENT, lastDeleteResponse.getStatusCode());
        boolean exists = bookRepo.findAll().stream().anyMatch(b -> b.getTitle().equals(title));
        assertFalse(exists);
    }
}
