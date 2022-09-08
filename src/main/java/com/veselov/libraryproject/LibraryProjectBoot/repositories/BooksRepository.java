package com.veselov.libraryproject.LibraryProjectBoot.repositories;

import com.veselov.libraryproject.LibraryProjectBoot.model.Book;
import com.veselov.libraryproject.LibraryProjectBoot.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository  extends JpaRepository<Book, Integer> {
    public List<Book> findByOwner(Person person);
    public Book findByNameStartsWith(String part);
}
