package com.santiago.bookstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Publisher {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "publisher")
    @ToString.Exclude
    private List<Book> bookList;
}
