# Bookstore

This Spring boot API application was created using Spring Web, Lombok, Data JPA and H2 as a RDBMS.

There are three models, all of which have their own routes:
- Authors
- Books
- Publishers

They all support CRUD operations.

## Interacting with the API

To obtain data a browser can be used, however, an API testing platform such as Postman is useful to make use of all the operations.

Routes are intuitive and consistent:
- /authors
- /books
- /publishers

The requests are as follows:
- GET "/" obtains a list of all elements. Example URL:
```
GET http://localhost:8080/books
```

- GET "/{id}" retrieves the entity with the provided ID, or a 404 error if it does not exist. Example URL:
```
GET http://localhost:8080/authors/1
```

- POST "/" creates a new record with the provided body as xxx-www-form-urlencoded. If no body is provided, a 400 error is returned. Example URL:
```
POST http://localhost:8080/publishers
```

- PUT "/{id}" updates a record with the provided body as xxx-www-form-urlencoded. If the ID has not been found, a 404 error is returned.
If no body is provided, a 400 error is returned. Example URL:
```
PUT http://localhost:8080/authors/3
```

- DELETE "/{id}" removes record with the provided ID. If the ID has not been found, a 404 error is returned.
Please note: If an Author or a Publisher that contains books, all books belonging to them will be removed before removing the Author or Publisher.
Example URL:
```
DELETE http://localhost:8080/books/2
```
