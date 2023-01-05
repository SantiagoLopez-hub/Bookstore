package com.santiago.bookstore.repo;

import com.santiago.bookstore.model.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepo extends CrudRepository<Publisher, Long> {
}
