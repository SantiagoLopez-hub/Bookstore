Feature: Book Management

  Scenario: Create a new book
    Given the database is initialized
    When I create a new author with name "J.K. Rowling"
    And I create a new publisher with name "Bloomsbury"
    And I create a new book with title "Harry Potter", price 29.99, isbn "9780747532743", author "J.K. Rowling", and publisher "Bloomsbury"
    Then the book should be created with title "Harry Potter"

  Scenario: Get a book by ID
    Given the database is initialized
    And a book exists with title "The Hobbit"
    When I request the book with title "The Hobbit"
    Then the response should contain title "The Hobbit"

  Scenario: Update a book
    Given the database is initialized
    And a book exists with title "Old Title"
    When I update the book "Old Title" with title "New Title"
    Then the book should have title "New Title"

  Scenario: Delete a book
    Given the database is initialized
    And a book exists with title "To Be Deleted"
    When I delete the book "To Be Deleted"
    Then the book "To Be Deleted" should no longer exist
